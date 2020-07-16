using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using PetVacay.Services;
using PetVacay.Helpers;
using EveryPay.Data.Repository;
using PetVacay.DTO;
using PetVacay.Exceptions;
using PetVacay.Data;
using System.Collections.Generic;
using System.Linq;

namespace PetVacay.Tests
{
    [TestClass]
    public class ClientLogicTest
    {
        #region RegisterClientTests
        [TestMethod]
        public void ValidatRegisterClient_1()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            int newId = service.RegisterNewClient(clientToAdd);
            service.DeleteClient(newId);
            Assert.IsInstanceOfType(newId, typeof(int));
        }

        [TestMethod]
        [ExpectedException(typeof(PasswordsDoNotMatchException))]
        public void ValidatRegisterClient_2()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength2";

            service.RegisterNewClient(clientToAdd);            
        }

        [TestMethod]
        [ExpectedException(typeof(WrongDataTypeException))]
        public void ValidatRegisterClient_3()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "va";
            clientToAdd.RepeatedPassword = "va";

            service.RegisterNewClient(clientToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(WrongDataTypeException))]
        public void ValidatRegisterClient_4()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMailmail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            service.RegisterNewClient(clientToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(WrongDataTypeException))]
        public void ValidatRegisterClient_5()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMailmail.com";
            clientToAdd.Password = "valid";
            clientToAdd.RepeatedPassword = "validLength";

            service.RegisterNewClient(clientToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidatRegisterClient_6()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMailmail.com";
            clientToAdd.RepeatedPassword = "validLength";

            service.RegisterNewClient(clientToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidatRegisterClient_7()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.RepeatedPassword = "validLength";
            service.RegisterNewClient(clientToAdd);
        }

        [TestMethod]
        [ExpectedException(typeof(InsufficientDataException))]
        public void ValidatRegisterClient_8()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMailmail.com";
            service.RegisterNewClient(clientToAdd);
        }

        [TestMethod]
        public void ValidatRegisterClient_9()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";
            int newId = service.RegisterNewClient(clientToAdd);

            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validPassword";
            clientToAdd.RepeatedPassword = "validPassword";

            try
            {
                service.RegisterNewClient(clientToAdd);
            }
            catch(UserAlreadyRegisteredException ex)
            {
                service.DeleteClient(newId);
                Assert.IsInstanceOfType(ex, typeof(UserAlreadyRegisteredException));
            }  
        }
        #endregion

        #region AddClientPets

        [TestMethod]
        [ExpectedException(typeof(UserNotFoundException))]
        public void ValidateAddClientPet_1()
        {
            IClientService service = new ClientService(new UnitOfWork());

            PetBundle petData = new PetBundle();
            petData.Name = "Rulito";
            service.AddClientPet(90, petData);
        }

        [TestMethod]
        public void ValidateAddClientPet_2()
        {
            IClientService service = new ClientService(new UnitOfWork());

            PetBundle petData = new PetBundle();
            petData.Age = 10;
            petData.Gender = "Masculino";
            petData.HasVaccination = true;
            petData.Weight = 50;
            petData.PetType = "Perro";
            petData.Information = "Perro bueno";
            petData.FriendlyPet = true;
            try
            {
                service.AddClientPet(2, petData);
            }
            catch(InsufficientDataException ex)
            {
                Assert.IsInstanceOfType(ex, typeof(InsufficientDataException));
            }      
        }

        public void ValidateAddClientPet_3()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            int newClientId = service.RegisterNewClient(clientToAdd);

            PetBundle petData = new PetBundle();
            petData.Name = "Rulito";
            petData.Age = 10;
            petData.Gender = "Masculino";
            petData.HasVaccination = true;
            petData.Weight = 50;
            petData.PetType = "Perro";
            petData.Information = "Perro bueno";
            petData.FriendlyPet = true;

            service.AddClientPet(newClientId, petData);

            try
            {
                service.AddClientPet(newClientId, petData);
            }
            catch (Exception)
            {
                Assert.Fail();
            }
            service.DeleteClient(newClientId);
        }
        #endregion

        #region UpdateClientInfo Tests

        [TestMethod]
        public void ValidateUpdateClient_1()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            int newId = service.RegisterNewClient(clientToAdd);

            ClientBundle dataToUpdate = new ClientBundle();
            dataToUpdate.Name = "valid";
            dataToUpdate.Surname = "valid";
            dataToUpdate.PhoneNumber = "095456908";
            dataToUpdate.Password = "validPassword";

            try
            {
                service.UpdateClientInfo(newId, dataToUpdate);
            }
            catch(Exception)
            {
                Assert.Fail();
            }
            service.DeleteClient(newId);
        }
        #endregion

        #region GetClientPets Tests

        [TestMethod]
        public void ValidateGetClientPets_1()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            int newId = service.RegisterNewClient(clientToAdd);

            IEnumerable<Pet> clientPets = service.GetClientPets(newId);
            Assert.AreEqual(0, clientPets.Count());

            service.DeleteClient(newId);
        }


        #endregion

        #region GetClientBookings Tests
        [TestMethod]
        public void ValidateGetClientBookings_1()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            int newId = service.RegisterNewClient(clientToAdd);

            IEnumerable<Booking> clientBookings = service.GetBookingsOfClient(newId);
            Assert.AreEqual(0, clientBookings.Count());

            service.DeleteClient(newId);
        }

        #endregion

        #region

        [TestMethod]
        public void ValidateGetClientInforms_1()
        {
            IClientService service = new ClientService(new UnitOfWork());

            ClientDTO clientToAdd = new ClientDTO();
            clientToAdd.Email = "validMail@mail.com";
            clientToAdd.Password = "validLength";
            clientToAdd.RepeatedPassword = "validLength";

            int newId = service.RegisterNewClient(clientToAdd);

            IEnumerable<Inform> clientInforms = service.GetClientInforms(newId);
            Assert.AreEqual(0, clientInforms.Count());

            service.DeleteClient(newId);
        }
        #endregion
    }
}
