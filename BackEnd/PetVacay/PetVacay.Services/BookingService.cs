using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PetVacay.DTO;
using EveryPay.Data.Repository;
using PetVacay.Data;
using PetVacay.Exceptions;
using PetVacay.Helpers.APIReturnClases;
using System.Text.RegularExpressions;

namespace PetVacay.Services
{
    public class BookingService : IBookingService
    {
        private readonly IUnitOfWork unitOfWork;

        public BookingService(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }

        public int RegisterNewBooking(BookingPaymentDTO bookingData)
        {
            PaymentDTO paymentDto = convertToPaymentDto(bookingData);

            BookingDTO bookingDto= convertToBookingDto(bookingData);

            validatePaymentData(paymentDto);

            Client c = unitOfWork.ClientRepository.GetByID(bookingData.ClientId);
            Worker w = unitOfWork.WorkerRepository.GetByID(bookingData.WorkerId);
            Pet p = unitOfWork.PetRepository.GetByID(bookingData.PetId);

            validateDataExists(c, w, p);
            validateBookingNotRepeated(bookingDto);
            validateWorkerBookingsNotOverlap(w, bookingData.StartDate, bookingData.FinishDate);
            validateWorkerBookingOnDisponibilities(w, bookingData.StartDate, bookingData.IsWalker);

            Booking newBooking = new Booking(bookingDto.ClientId, bookingDto.WorkerId, bookingDto.PetId,
                bookingDto.StartDate, bookingDto.FinishDate, bookingDto.IsWalker);
            newBooking.MadeDate = DateTime.Now;
            newBooking.InitialLocation = new Location();

            unitOfWork.BookingsRepository.Insert(newBooking);
            unitOfWork.Save();

            Payment newPayment = new Payment(newBooking.BookingId, paymentDto.CreditCardNumber, paymentDto.CCV, paymentDto.CreditCardExpirationMonth, paymentDto.CreditCardExpirationYear, paymentDto.Amount);
            unitOfWork.PaymentRepository.Insert(newPayment);
            unitOfWork.Save();

            return newBooking.BookingId;
        }


        private PaymentDTO convertToPaymentDto(BookingPaymentDTO bookingData)
        {
            PaymentDTO paymentDto = new PaymentDTO();
            paymentDto.CreditCardNumber = bookingData.CreditCardNumber;
            paymentDto.CreditCardExpirationYear = bookingData.CreditCardExpirationYear;
            paymentDto.CreditCardExpirationMonth = bookingData.CreditCardExpirationMonth;
            paymentDto.CCV = bookingData.CCV;
            paymentDto.Amount = bookingData.Amount;
            return paymentDto;
        }

        private BookingDTO convertToBookingDto(BookingPaymentDTO bookingData)
        {
            BookingDTO bookingDto = new BookingDTO();
            bookingDto.ClientId = bookingData.ClientId;
            bookingDto.FinishDate = bookingData.FinishDate;
            bookingDto.IsWalker = bookingData.IsWalker;
            bookingDto.PetId = bookingData.PetId;
            bookingDto.StartDate = bookingData.StartDate;
            bookingDto.WorkerId = bookingData.WorkerId;

            return bookingDto;
        }

        private void validatePaymentData(PaymentDTO payment)
        {
            var cvvCheck = new Regex(@"^\d{3}$");
           
            if (payment.CreditCardNumber.Length!=16 || !cvvCheck.IsMatch(payment.CCV.ToString()) || payment.CreditCardExpirationMonth>12 || payment.CreditCardExpirationMonth<1 || payment.Amount<1)
            {
                throw new InsufficientDataException("Datos del pago mal ingresados, vuelva a ingresarlos");
            }
        }

        private void validateDataExists(Client c, Worker w, Pet p)
        {
            if(c == null || w == null || p == null)
            {
                throw new UserNotFoundException("El cliente, el trabajador o la mascota no existe.");
            }
        }

        private void validateBookingNotRepeated(BookingDTO bookingData)
        {
            IEnumerable<Booking> bookings = unitOfWork.BookingsRepository.Get(b => b.ClientId == bookingData.ClientId
                                            && b.WorkerId == bookingData.WorkerId && b.PetId == bookingData.PetId

                                            && b.StartDate.Day == bookingData.StartDate.Day
                                            && b.StartDate.Month == bookingData.StartDate.Month
                                            && b.StartDate.Year == bookingData.StartDate.Year

                                            && b.FinishDate.Day == bookingData.FinishDate.Day
                                            && b.FinishDate.Month == bookingData.FinishDate.Month
                                            && b.FinishDate.Year == bookingData.FinishDate.Year);

            if(bookings.Count() > 0)
            {
                throw new BookingAlreadyRegisteredException("Ya existe una reserva realizada para dicho trabajador en el mismo periodo.");
            }
        }

        private void validateWorkerBookingsNotOverlap(Worker w, DateTime startDate, DateTime finishDate)
        {
            IEnumerable<Booking> bookingsOfWorker = unitOfWork.BookingsRepository.Get(b => b.WorkerId == w.WorkerId);

            IEnumerable<Booking> bookingsOverlaping = bookingsOfWorker.Where(b => datesOverlap(b.StartDate, b.FinishDate, startDate, finishDate));

            if(bookingsOverlaping.Count() > 0)
            {
                throw new DatesOverlapException("El trabajador ya tiene agendada una reserva para ese periodo.");
            }
        }

        private bool datesOverlap(DateTime startDateBD, DateTime finishDateBD, DateTime startDateNew, DateTime finishDateNew)
        {
            bool datesOverlap = startDateBD < finishDateNew && startDateNew < finishDateBD;
            return datesOverlap;
        }

        private void validateWorkerBookingOnDisponibilities(Worker worker, DateTime date, bool isWalkingType)
        {
            if(isWalkingType)
            {
                List<DayOfWeek> workerDisponibilities = convertDisponibilities(worker.Disponibility);

                if(!workerDisponibilities.Contains(date.DayOfWeek))
                {
                    throw new DisponibilitiesNotMatchException("El trabajador no trabaja ese dia de la semana.");
                }
            }
        }

        public void DeleteBooking(int bookingId)
        {
            Booking b = unitOfWork.BookingsRepository.GetByID(bookingId);
            validateBookingExists(b);
            if(canCancelBooking(b))
            {
                unitOfWork.BookingsRepository.Delete(b);
                unitOfWork.Save();
            }
            else
            {
                throw new TimeExceededException("No se puede cancelar una reserva con mas de 24 horas de haber sido realizada.");
            }
        }

        private void validateBookingExists(Booking b)
        {
            if(b == null)
            {
                throw new BookingNotFoundException("No se ha encontrado dicha reserva.");
            }
        }

        private bool canCancelBooking(Booking b)
        {
            bool canCancelBooking = true;
            DateTime dateBookingWasMade = b.MadeDate;
            DateTime currentDate = DateTime.Now;
            double differenceInHours = (currentDate - dateBookingWasMade).TotalHours;

            if (differenceInHours > 24 || b.WalkingStarted)
            {
                canCancelBooking = false;
            }
            return canCancelBooking;
        }

        public WalkingLocationToReturn GetBookingWorkerLocations(int bookingId)
        {
            Booking booking = unitOfWork.BookingsRepository.GetByID(bookingId);
            validateBookingExists(booking);

            int workerId = booking.WorkerId;
            Worker workerOfBooking = unitOfWork.WorkerRepository.GetByID(workerId);

            Location initialL = booking.InitialLocation;
            Location currentL = workerOfBooking.Location;
            WalkingLocationToReturn workerLocations = new WalkingLocationToReturn(initialL.Latitude, initialL.Longitude, currentL.Latitude, currentL.Longitude);

            return workerLocations;
        }

        public void UpdateWalkingInitialLocation(int bookingId, Location location)
        {
            Booking b = unitOfWork.BookingsRepository.GetByID(bookingId);
            validateBookingExists(b);
            b.InitialLocation = location;
            b.WalkingStarted = true;
            unitOfWork.BookingsRepository.Update(b);
            unitOfWork.Save();
        }

        public IEnumerable<DateTime> GetWorkerAvailableDaysToMakeInform(int bookingId)
        {
            Booking b = unitOfWork.BookingsRepository.GetByID(bookingId);
            validateBookingExists(b);
            Worker bookingWorker = unitOfWork.WorkerRepository.GetByID(b.WorkerId);
            return getDayToInform(b.StartDate, b.FinishDate);
        }
      
        private List<DateTime> getDayToInform(DateTime startDate, DateTime finishDate)
        {
            List<DateTime> days = new List<DateTime>();

            DateTime today = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day);
            DateTime start = new DateTime(startDate.Year, startDate.Month, startDate.Day);
            DateTime finish = new DateTime(finishDate.Year, finishDate.Month, finishDate.Day);

            if (today >= start && today <= finish)
            {
                days.Add(DateTime.Now);
            }
            return days;
        }

        private List<DayOfWeek> convertDisponibilities(List<WeekDay> disponibility)
        {
            List<DayOfWeek> days = new List<DayOfWeek>();
            foreach(WeekDay w in disponibility)
            {
                DayOfWeek d = w.Day;
                days.Add(d);
            }
            return days;
        }
    }
}
