using EveryPay.Data.Repository;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using PetVacay.Data;
using PetVacay.DTO;
using PetVacay.Exceptions;
using PetVacay.Helpers;
using PetVacay.Helpers.APIReturnClases;
using PetVacay.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Tests
{
    [TestClass]
    public class WorkerLogicTest
    {
        #region RegisterWorkerTests
        [TestMethod]
        public void ValidateRegisterWorker_1()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail@mail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = true;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";


            int newId = service.RegisterNewWorker(workerToAdd);
            service.DeleteWorker(newId);
            Assert.IsInstanceOfType(newId, typeof(int));          
        }

        [TestMethod]
        [ExpectedException(typeof(PasswordsDoNotMatchException))]
        public void ValidateRegisterWorker_2()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail@mail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength2";
            workerToAdd.IsWalker = true;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";

            service.RegisterNewWorker(workerToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(WrongDataTypeException))]
        public void ValidateRegisterWorker_3()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail@mail.com";
            workerToAdd.Password = "va";
            workerToAdd.RepeatedPassword = "va";
            workerToAdd.IsWalker = false;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";

            service.RegisterNewWorker(workerToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(WrongDataTypeException))]
        public void ValidateRegisterWorker_4()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMailmail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = false;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";

            service.RegisterNewWorker(workerToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(WrongDataTypeException))]
        public void ValidateRegisterWorker_5()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMailmail.com";
            workerToAdd.Password = "valid";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = true;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";

            service.RegisterNewWorker(workerToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidateRegisterWorker_6()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "valid@Mailmail.com";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";
            workerToAdd.IsWalker = true;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };

            service.RegisterNewWorker(workerToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidateRegisterWorker_7()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.RepeatedPassword = "validLength";

            service.RegisterNewWorker(workerToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidateRegisterWorker_8()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMailmail.com";
            service.RegisterNewWorker(workerToAdd);
        }

        [TestMethod]
        public void ValidateRegisterWorker_9()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "valid@Mailmail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = false;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";

            int newId = service.RegisterNewWorker(workerToAdd);

            WorkerBundle workerToUpdate = new WorkerBundle();
            workerToUpdate.Password = "validPassword";
            workerToUpdate.PhoneNumber = "222333444";

            try
            {
                service.UpdateWorkerInfo(newId, workerToUpdate);
            }
            catch(Exception ex)
            {
                Assert.Fail();
            }
            service.DeleteWorker(newId);
        }
        #endregion


        #region UpdateWorkerInfo Tests

        [TestMethod]
        public void ValidateUpdateClient_1()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail@mail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = false;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";


            int newId = service.RegisterNewWorker(workerToAdd);


            WorkerBundle dataToUpdate = new WorkerBundle();
            dataToUpdate.Name = "valid";
            dataToUpdate.Surname = "valid";
            dataToUpdate.PhoneNumber = "095456908";
            dataToUpdate.Password = "validPassword";
            dataToUpdate.Address = "address";
            dataToUpdate.BoardingPrice = 100;
            dataToUpdate.Information = "information";
            dataToUpdate.ShortDescription = "description";

            try
            {
                service.UpdateWorkerInfo(newId, dataToUpdate);
            }
            catch (Exception)
            {
                Assert.Fail();
            }
            service.DeleteWorker(newId);
        }


        #endregion

        #region GetWorkerBookings Tests

        [TestMethod]
        public void ValidateGetBookings_1()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail@mail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = false;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";
            int newId = service.RegisterNewWorker(workerToAdd);


            IEnumerable<Booking> bookings = service.GetWorkerBookings(newId);
            service.DeleteWorker(newId);
            Assert.AreEqual(0, bookings.Count());
        }

        #endregion

        #region GetWorkerReviews Tests

        [TestMethod]
        public void ValidateGetReviews_1()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail@mail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = false;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";
            int newId = service.RegisterNewWorker(workerToAdd);

            try
            {
                IEnumerable<ReviewToReturn> reviews = service.GetWorkerReviews(newId);
                
            }
            catch(NoReviewsForWorkerException ex)
            {
                Assert.IsInstanceOfType(ex, typeof(NoReviewsForWorkerException));
                service.DeleteWorker(newId);
            }
        }

        #endregion

        #region GetWorkerAvailableDaysCount Tests

        [TestMethod]
        public void ValidateGetAvailableDaysCount_1()
        {
            IWorkerService service = new WorkerService(new UnitOfWork());

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail@mail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = false;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";
            int newId = service.RegisterNewWorker(workerToAdd);

            try
            {
                int count = service.GetWorkerAvailableInformsCount(newId);

            }
            catch (WorkerHasNotBookingsToInformException ex)
            {
                Assert.IsInstanceOfType(ex, typeof(WorkerHasNotBookingsToInformException));
                service.DeleteWorker(newId);
            }
        }

        #endregion
    }
}