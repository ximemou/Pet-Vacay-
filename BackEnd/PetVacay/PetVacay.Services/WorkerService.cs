using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PetVacay.DTO;
using PetVacay.Helpers;
using EveryPay.Data.Repository;
using PetVacay.Validators;
using PetVacay.Data;
using System.IO;
using PetVacay.Helpers.APIReturnClases;
using PetVacay.Exceptions;
using PetVacay.Enumerators;
using System.Net;
using System.Xml.Linq;
using PetVacay.Adapters;

namespace PetVacay.Services
{
    public class WorkerService : IWorkerService
    {
        private readonly IUnitOfWork unitOfWork;
        private readonly IUsersValidator validator;
        private readonly IGeocoderAdapter localizationMgr;

        public WorkerService(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
            validator = new WorkerValidator(unitOfWork);
            localizationMgr = new GeocoderAdapter();
        }

        public int RegisterNewWorker(WorkerDTO newWorker)
        {
            validateNewWorkerDataIsCorrect(newWorker);
            validator.validateUserIsNotRegistered(null, newWorker);

            Worker workerToAdd = convertDTO(newWorker);
            unitOfWork.WorkerRepository.Insert(workerToAdd);
            unitOfWork.Save();

            return workerToAdd.WorkerId;
        }

        private void validateNewWorkerDataIsCorrect(WorkerDTO newWorker)
        {
            validator.validateHasAllFields(null, newWorker);
            validator.validateAllFieldsHaveCorrectType(null, newWorker);
            validator.validatePasswordsMatch(null, newWorker);
        }

        private Worker convertDTO(WorkerDTO newWorker)
        {
            Worker worker = new Worker(newWorker.Email, newWorker.Password, newWorker.IsWalker,
                convertToWeekDayList(newWorker.Disponibility), 0, "", "");
            worker.Location = new Location(newWorker.Latitude, newWorker.Longitude);
            return worker;
        }

        private List<WeekDay> convertToWeekDayList(List<DayOfWeek> disponibility)
        {
            List<WeekDay> converted = new List<WeekDay>();
            foreach (DayOfWeek day in disponibility)
            {
                converted.Add(new WeekDay(day));
            }
            return converted;
        }

        public void UpdateWorkerInfo(int workerId, WorkerBundle workerInfo)
        {
            Worker workerToUpdate = unitOfWork.WorkerRepository.GetByID(workerId);

            validator.validateUserExists(null, workerToUpdate);
            validateWorkerDataToUpdate(workerInfo);

            updateNewInfo(workerToUpdate, workerInfo);

            unitOfWork.WorkerRepository.Update(workerToUpdate);
            unitOfWork.Save();
        }

        private void validateWorkerDataToUpdate(WorkerBundle newWorkerInfo)
        {
            if (newWorkerInfo.Name != null)
            {
                validator.validateStringData(null, newWorkerInfo);
                validator.validatePhoneNumber(null, newWorkerInfo);
            }
        }

        private void updateNewInfo(Worker workerToUpdate, WorkerBundle newWorkerInfo)
        {
            if(newWorkerInfo.Name != null)
            {
                workerToUpdate.Name = newWorkerInfo.Name;
                workerToUpdate.Surname = newWorkerInfo.Surname;
                workerToUpdate.Password = newWorkerInfo.Password;
                workerToUpdate.PhoneNumber = newWorkerInfo.PhoneNumber;
            }

            if(newWorkerInfo.ShortDescription != null)
            {          
                workerToUpdate.ShortDescription = newWorkerInfo.ShortDescription;
                workerToUpdate.Information = newWorkerInfo.Information;
                workerToUpdate.BoardingPrice = newWorkerInfo.BoardingPrice;
                workerToUpdate.ZIPCode = localizationMgr.GetZipCodeFromAddress(newWorkerInfo.Address);
            }
            if(newWorkerInfo.Disponibility != null)
            {
                workerToUpdate.Disponibility = convertDisponibilityIntToString(newWorkerInfo.Disponibility, workerToUpdate.WorkerId);
            }
            if(newWorkerInfo.WalkingPrice != null && newWorkerInfo.WalkingPrice != 0)
            {
                workerToUpdate.WalkingPrice = newWorkerInfo.WalkingPrice;
            }

            if (newWorkerInfo.ProfileImage != null)
                workerToUpdate.ProfileImage = ImageSaver.GetIntance().getUrlOfImage(newWorkerInfo.ProfileImage, workerToUpdate.WorkerId.ToString(), "w");
        }

        private List<WeekDay> convertDisponibilityIntToString(List<int> disponibility, int workerId)
        {
            removeOldDisponibilitiesFromDB(workerId);

            List<WeekDay> newDisponibility = new List<WeekDay>();
            foreach (int d in disponibility)
            {
                DayOfWeek day = (DayOfWeek)d;
                WeekDay weekDay = new WeekDay(day);
                newDisponibility.Add(weekDay);
            }
            return newDisponibility;
        }

        private void removeOldDisponibilitiesFromDB(int workerId)
        {
            IEnumerable<WeekDay> disponibilitiesToDelete = unitOfWork.DisponibilitiesRepository.Get(d => d.WorkerId == workerId);
            foreach(WeekDay w in disponibilitiesToDelete)
            {
                unitOfWork.DisponibilitiesRepository.Delete(w);
            }
            unitOfWork.Save();
        }

        public void AddWorkerImage(int workerId, ImageBundle newImage)
        {

            Worker workerToAddImage = unitOfWork.WorkerRepository.GetByID(workerId);
            validator.validateUserExists(null, workerToAddImage);

            string newImgUrl = ImageSaver.GetIntance().getUrlOfImage(newImage.Image, Guid.NewGuid().ToString(), "w");

            WorkerImage imageToAdd = new WorkerImage(newImgUrl);

            workerToAddImage.Images.Add(imageToAdd);          
            unitOfWork.Save();
        }

        public IEnumerable<ImageToReturn> getWorkerImages(int workerId)
        {
            Worker workerToAddImage = unitOfWork.WorkerRepository.GetByID(workerId);
            validator.validateUserExists(null, workerToAddImage);

            List<ImageToReturn> images = new List<ImageToReturn>();
            foreach(WorkerImage img in workerToAddImage.Images)
            {
                byte[] convertedImg = ImageSaver.GetIntance().getImage(img.ImageUrl);
                ImageToReturn retImg = new ImageToReturn(convertedImg);
                images.Add(retImg);
            }
            return images;
        }

        public WorkerToReturn GetWorker(int workerId)
        {
            if (ExistsWorker(workerId))
            {
                Worker worker = unitOfWork.WorkerRepository.GetByID(workerId);
                List<string> disponibility = convertDisponibilityEnum(worker.Disponibility);

                WorkerToReturn retWorker = convertWorkerToReturn(worker, disponibility);
                return retWorker;
            }
            else
            {
                throw new UserNotFoundException("No existe el usuario");
            }
        }

        private bool ExistsWorker(int workerId)
        {
            return unitOfWork.WorkerRepository.Get(w => w.WorkerId == workerId).FirstOrDefault() != null;
        }

        private List<string> convertDisponibilityEnum(List<WeekDay> disponibility)
        {
            List<string> daysAsString = new List<string>();
            foreach(WeekDay d in disponibility)
            {
                string dayName = d.Day.ToString();
                daysAsString.Add(dayName);
            }
            return daysAsString;
        }

        private WorkerToReturn convertWorkerToReturn(Worker worker, List<string> disponibility)
        {
            byte[] workerImg = ImageSaver.GetIntance().getImage(worker.ProfileImage);

            IEnumerable<WorkerImage> workerImages = unitOfWork.WorkerImagesRepository.Get(i => i.WorkerId == worker.WorkerId);

            byte[] bannerImage = null;
            if(workerImages.Count() > 0)
            {
                bannerImage = ImageSaver.GetIntance().getImage(workerImages.ElementAt(0).ImageUrl);
            }

            LocationBundle workerLocation = convertLocationToReturn(worker.Location);
            double workerScore = getWorkerAverageScore(worker.WorkerId);


            WorkerToReturn retWorker = new WorkerToReturn(worker.WorkerId, worker.Name, worker.Surname, 
                worker.Email, worker.Password, worker.PhoneNumber, worker.IsWalker, disponibility, workerImg, 
                worker.ShortDescription, worker.Information, worker.BoardingPrice, worker.WalkingPrice, 
                bannerImage, workerLocation, workerScore);
            return retWorker;
        }

        public IEnumerable<WorkerToReturn> GetWorkersByFilter(bool both, string name, string address)
        {
            int zipCode = 0;
            if (address != null)
            {
                zipCode = localizationMgr.GetZipCodeFromAddress(address);
            }
            IEnumerable<Worker> query = unitOfWork.WorkerRepository.Get(w => (w.IsWalker == both) &&
                                                (name == null || w.Name == name) && 
                                                (address == null || w.ZIPCode == zipCode));

            List<WorkerToReturn> queryWorkers = new List<WorkerToReturn>();
            foreach(Worker worker in query)
            {
                List<string> disponibilities = convertDisponibilityEnum(worker.Disponibility);
                WorkerToReturn workerRet = convertWorkerToReturn(worker, disponibilities);
                queryWorkers.Add(workerRet);
            }
            return queryWorkers;

        }

        public void UpdateWorkerLocation(int workerId, LocationBundle newLocation)
        {
            Worker workerToUpdate = unitOfWork.WorkerRepository.GetByID(workerId);
            validator.validateUserExists(null, workerToUpdate);

            Location workerLocation = convertLocation(newLocation);

            workerToUpdate.Location = workerLocation;
            unitOfWork.WorkerRepository.Update(workerToUpdate);
            unitOfWork.Save();
        }

        private Location convertLocation(LocationBundle newLocation)
        {
            Location workerLocation = new Location(newLocation.Latitude, newLocation.Longitude);
            return workerLocation;
        }

        private LocationBundle convertLocationToReturn(Location location)
        {
            LocationBundle workerLocation = new LocationBundle(location.Latitude, location.Longitude);
            return workerLocation;
        }

        public IEnumerable<WorkerToReturn> GetWorkers()
        {
            IEnumerable<Worker> query = unitOfWork.WorkerRepository.Get();

            List<WorkerToReturn> queryWorkers = new List<WorkerToReturn>();
            foreach (Worker worker in query)
            {
                List<string> disponibilities = convertDisponibilityEnum(worker.Disponibility);
                WorkerToReturn workerRet = convertWorkerToReturn(worker, disponibilities);
                queryWorkers.Add(workerRet);
            }
            return queryWorkers;
        }

        public IEnumerable<Booking> GetWorkerBookings(int workerId)
        {
            Worker w = unitOfWork.WorkerRepository.GetByID(workerId);
            validator.validateUserExists(null, w);

            IEnumerable<Booking> workerBookings = unitOfWork.BookingsRepository.Get(b => b.WorkerId == workerId);
            return workerBookings;
        }

        private double getWorkerAverageScore(int workerId)
        {
            IEnumerable<Booking> workerBookings = unitOfWork.BookingsRepository.Get(b => b.WorkerId == workerId);
            int scoresSum = 0;
            int totalReviews = 0;

            foreach(Booking b in workerBookings)
            {
                Review bookingReview = unitOfWork.ReviewsRepository.Get(r => r.BookingId == b.BookingId).FirstOrDefault();

                if(bookingReview != null)
                {
                    scoresSum += bookingReview.Score;
                    totalReviews++;
                }
            }

            if(totalReviews > 0)
            {
                return (double) scoresSum / (double) totalReviews;
            }
            return 0;
        }

        public IEnumerable<ReviewToReturn> GetWorkerReviews(int workerId)
        {
            List<ReviewToReturn> workerReviews = getWorkerReviews(workerId);
            return workerReviews;
        }

        private List<ReviewToReturn> getWorkerReviews(int workerId)
        {
            IEnumerable<Booking> workerBookings = unitOfWork.BookingsRepository.Get(b => b.WorkerId == workerId);
            List<ReviewToReturn> reviews = new List<ReviewToReturn>();

            foreach (Booking b in workerBookings)
            {
                Review bookingReview = unitOfWork.ReviewsRepository.Get(r => r.BookingId == b.BookingId).FirstOrDefault();

                if (bookingReview != null)
                {
                    ReviewToReturn review = new ReviewToReturn(bookingReview.Score, bookingReview.Comment);
                    reviews.Add(review);
                }
            }
            
            if(reviews.Count == 0)
            {
                throw new NoReviewsForWorkerException("El trabajador no tiene ninguna calificacion todavia.");
            }

            return reviews;
        }

        public void DeleteWorker(int workerId)
        {
            Worker w = unitOfWork.WorkerRepository.GetByID(workerId);
            validator.validateUserExists(null, w);
            validateCanDeleteWorker(w);
            unitOfWork.WorkerRepository.Delete(w);
            unitOfWork.Save();
        }

        private void validateCanDeleteWorker(Worker w)
        {
            IEnumerable<Booking> workerBookings = unitOfWork.BookingsRepository.Get(b => b.WorkerId == w.WorkerId);
            if(workerBookings.Count() > 0)
            {
                throw new UserHasBookingsException("El usuario tiene reservas registradas.");
            }
        }

        public int GetWorkerAvailableInformsCount(int workerId)
        {
            Worker w = unitOfWork.WorkerRepository.GetByID(workerId);
            validator.validateUserExists(null, w);
            int workerAvailableBookingInforms = 0;

            IEnumerable<Booking> workerBookings = unitOfWork.BookingsRepository.Get(b => b.WorkerId == workerId);
            DateTime today = DateTime.Now;

            foreach(Booking b in workerBookings)
            {
                if (today.Date >= b.StartDate.Date && today.Date <= b.FinishDate.Date)
                {
                    workerAvailableBookingInforms++;
                }
            }

            if(workerAvailableBookingInforms == 0)
            {
                throw new WorkerHasNotBookingsToInformException("El trabajador no tiene reservas que informar para este dia.");
            }

            return workerAvailableBookingInforms;

        }
    }
}
