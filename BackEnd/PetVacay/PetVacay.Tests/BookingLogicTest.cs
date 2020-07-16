using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using PetVacay.Services;
using EveryPay.Data.Repository;
using PetVacay.DTO;
using System.Collections.Generic;
using PetVacay.Exceptions;
using PetVacay.Helpers;

namespace PetVacay.Tests
{
    [TestClass]
    public class BookingLogicTest
    {

        public void ValidateRegisterBooking_1()
        {
            IClientService clientService = new ClientService(new UnitOfWork());
            IWorkerService workerService = new WorkerService(new UnitOfWork());
            IBookingService bookingService = new BookingService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            int newClientId = clientService.RegisterNewClient(clientToAdd);

            PetBundle petData = new PetBundle();
            petData.Name = "Rulito";
            petData.Age = 10;
            petData.Gender = "Masculino";
            petData.HasVaccination = true;
            petData.Weight = 50;
            petData.PetType = "Perro";
            petData.Information = "Perro bueno";
            petData.FriendlyPet = true;

            clientService.AddClientPet(newClientId, petData);

            WorkerDTO workerToAdd = new WorkerDTO();
            workerToAdd.Email = "validMail2@mail.com";
            workerToAdd.Password = "validLength";
            workerToAdd.RepeatedPassword = "validLength";
            workerToAdd.IsWalker = true;
            workerToAdd.Disponibility = new List<DayOfWeek>() { 0 };
            workerToAdd.Latitude = "validLength";
            workerToAdd.Longitude = "validLength";

            int newWorkerId = workerService.RegisterNewWorker(workerToAdd);

            BookingPaymentDTO bookingToAdd = new BookingPaymentDTO();
            bookingToAdd.ClientId = newClientId;
            bookingToAdd.WorkerId = newWorkerId;
            bookingToAdd.StartDate = DateTime.Now;
            bookingToAdd.FinishDate = DateTime.Now;
            bookingToAdd.IsWalker = true;
            bookingToAdd.CreditCardNumber = "1112222444555557";
            bookingToAdd.CCV = 222;
            bookingToAdd.CreditCardExpirationMonth = 5;
            bookingToAdd.CreditCardExpirationYear = 2022;
            bookingToAdd.Amount = 200;

            int newBookingId = bookingService.RegisterNewBooking(bookingToAdd);

            bookingService.DeleteBooking(newBookingId);
            clientService.DeleteClient(newClientId);
            workerService.DeleteWorker(newWorkerId);

            Assert.IsInstanceOfType(newBookingId, typeof(int));
        }

        [TestMethod]
        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidateRegisterBooking_2()
        {
            IBookingService bookingService = new BookingService(new UnitOfWork());

            BookingPaymentDTO bookingToAdd = new BookingPaymentDTO();
            bookingToAdd.ClientId = 10;
            bookingToAdd.WorkerId = 10;
            bookingToAdd.StartDate = DateTime.Now;
            bookingToAdd.FinishDate = DateTime.Now;
            bookingToAdd.IsWalker = true;
            bookingToAdd.CreditCardNumber = "11122224445557";
            bookingToAdd.CCV = 222;
            bookingToAdd.CreditCardExpirationMonth = 5;
            bookingToAdd.CreditCardExpirationYear = 2022;
            bookingToAdd.Amount = 200;

            int newBookingId = bookingService.RegisterNewBooking(bookingToAdd);

            bookingService.DeleteBooking(newBookingId);

            Assert.IsInstanceOfType(newBookingId, typeof(int));
        }


        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidateRegisterBooking_3()
        {
            IBookingService bookingService = new BookingService(new UnitOfWork());

            BookingPaymentDTO bookingToAdd = new BookingPaymentDTO();
            bookingToAdd.ClientId = 10;
            bookingToAdd.WorkerId = 10;
            bookingToAdd.StartDate = DateTime.Now;
            bookingToAdd.FinishDate = DateTime.Now;
            bookingToAdd.IsWalker = true;
            bookingToAdd.CreditCardNumber = "1112222444555778";
            bookingToAdd.CCV = 222;
            bookingToAdd.CreditCardExpirationMonth = 5;
            bookingToAdd.CreditCardExpirationYear = 2022;
            bookingToAdd.Amount = 200;

            int newBookingId = bookingService.RegisterNewBooking(bookingToAdd);

            bookingService.DeleteBooking(newBookingId);

            Assert.IsInstanceOfType(newBookingId, typeof(int));
        }
    }
}
