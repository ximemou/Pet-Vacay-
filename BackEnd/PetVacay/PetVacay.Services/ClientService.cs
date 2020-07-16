using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PetVacay.DTO;
using PetVacay.Helpers;
using EveryPay.Data.Repository;
using PetVacay.Data;
using PetVacay.Validators;
using PetVacay.Exceptions;
using System.IO;
using PetVacay.Helpers.APIReturnClases;
using PetVacay.Enumerators;

namespace PetVacay.Services
{
    public class ClientService : IClientService
    {
        private readonly IUnitOfWork unitOfWork;
        private readonly IUsersValidator validator;
        private readonly IPetValidator petValidator;

        public ClientService(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
            validator = new ClientValidator(unitOfWork);
            petValidator = new PetValidator(unitOfWork);

        }

        public void AddClientPet(int clientId, PetBundle petData)
        {
            validatePetData(clientId, petData);
            Pet petToAdd = convertDTOPet(petData);

            Client clientToAddPet = unitOfWork.ClientRepository.GetByID(clientId);
            validator.validateUserExists(clientToAddPet);

          
            clientToAddPet.PersonalPets.Add(petToAdd);
            unitOfWork.Save();
        }

        private void validatePetData(int clientId, PetBundle petData)
        {
                petValidator.validateHasAllFields(petData);
                petValidator.validateCorrectTypeFields(petData);
                petValidator.validatePetNotAlreadyAdded(clientId, petData);
  
        }

        private Pet convertDTOPet(PetBundle petData)
        {
            Pet pet = new Pet(petData.Name, petData.PetType,petData.Gender,petData.Information, petData.Age, petData.Weight, petData.FriendlyPet,
                petData.HasVaccination);
            if(petData.PetImage != null)
                pet.PetImage = ImageSaver.GetIntance().getUrlOfImage(petData.PetImage, Guid.NewGuid().ToString() , "p");
            return pet;
        }

        public int RegisterNewClient(ClientDTO newClient)
        {
            validateNewClientDataIsCorrect(newClient);
            validator.validateUserIsNotRegistered(newClient);

            Client clientToAdd = convertDTO(newClient);
            unitOfWork.ClientRepository.Insert(clientToAdd);
            unitOfWork.Save();

            return clientToAdd.ClientId;
        }     

        private void validateNewClientDataIsCorrect(ClientDTO newClient)
        {
            validator.validateHasAllFields(newClient);
            validator.validateAllFieldsHaveCorrectType(newClient);
            validator.validatePasswordsMatch(newClient);
        }
        
        private Client convertDTO(ClientDTO newClient)
        {
            Client client = new Client(newClient.Email, newClient.Password);
            return client;
        }

        public void UpdateClientInfo(int clientId, ClientBundle newClientInfo)
        {
            Client clientToUpdate = unitOfWork.ClientRepository.GetByID(clientId);

            validator.validateUserExists(clientToUpdate);
            validateClientDataToUpdate(newClientInfo);

            updateNewInfo(clientToUpdate, newClientInfo);

            unitOfWork.ClientRepository.Update(clientToUpdate);
            unitOfWork.Save();
        }

        private void validateClientDataToUpdate(ClientBundle newClientInfo)
        {
            validator.validateStringData(newClientInfo);
            validator.validatePhoneNumber(newClientInfo);
        }

        private void updateNewInfo(Client clientToUpdate, ClientBundle newClientInfo)
        {
            clientToUpdate.Name = newClientInfo.Name;
            clientToUpdate.Surname = newClientInfo.Surname;
            clientToUpdate.Password = newClientInfo.Password;
            clientToUpdate.PhoneNumber = newClientInfo.PhoneNumber;
        }

        public IEnumerable<Pet> GetClientPets(int clientId)
        {
            if (ExistsClient(clientId))
            {
              return unitOfWork.PetRepository.Get(p => p.ClientId == clientId);
            }
            else
            {
                throw new UserNotFoundException("No existe el usuario");
            }

        }

        public ClientToReturn GetClient(int clientId)
        {
            if (ExistsClient(clientId))
            {
                Client client = unitOfWork.ClientRepository.GetByID(clientId);
                List <PetToReturn> pets= getClientPetsWithImage(client);
                ClientToReturn retClient = convertClientToReturn(client, pets);
                return retClient;
            }
            else
            {
                throw new UserNotFoundException("No existe el usuario");
            }
        }

        private List<PetToReturn> getClientPetsWithImage(Client client)
        {
            List<PetToReturn> clientPetsWithImage = new List<PetToReturn>();
            List<Pet> pets = client.PersonalPets;
            foreach(Pet pet in pets)
            {
                byte[] petImg = ImageSaver.GetIntance().getImage(pet.PetImage);
                string petType = pet.PetType;
                PetToReturn petWithImage = new PetToReturn(pet.PetId, pet.Name, petType, petImg,pet.Gender, pet.Age, pet.Weight, pet.FriendlyPet, pet.HasVaccination,pet.Information);
                clientPetsWithImage.Add(petWithImage);
            }
            return clientPetsWithImage;
        }

        private ClientToReturn convertClientToReturn(Client client, List<PetToReturn> pets)
        {
            byte[] clientImg = ImageSaver.GetIntance().getImage(client.ProfileImage);
            ClientToReturn clientToReturn = new ClientToReturn(client.ClientId, client.Name, client.Surname,
                client.Email, client.Password, clientImg, pets, client.PhoneNumber);

            return clientToReturn;
        }

        public IEnumerable<Client> GetClients()
        {
            return unitOfWork.ClientRepository.Get();
        }

        public byte [] GetClientImage(int clientId)
        {
            Client client = unitOfWork.ClientRepository.GetByID(clientId);
            string imgUrl = client.ProfileImage;
            byte [] profileImage = ImageSaver.GetIntance().getImage(imgUrl);
            return profileImage;
        }


        private bool ExistsClient(int clientId)
        {
            return unitOfWork.ClientRepository.Get(c => c.ClientId == clientId).FirstOrDefault() != null;
        }


        public void AddClientImage(int clientId, ImageBundle newImage)
        {

            Client clientToAddImage = unitOfWork.ClientRepository.GetByID(clientId);
            validator.validateUserExists(clientToAddImage,null);
            clientToAddImage.ProfileImage = ImageSaver.GetIntance().getUrlOfImage(newImage.Image, clientToAddImage.ClientId.ToString(), "c");
            unitOfWork.Save();
        }

        public PetToReturn GetClientPet(int clientId, int petId)
        {

            if (ExistsClient(clientId))
            {
               Pet pet= unitOfWork.PetRepository.Get(p => p.ClientId == clientId && p.PetId==petId).FirstOrDefault();
                PetToReturn petToReturn = convertPetToReturn(pet);
                return petToReturn;
            }
            else
            {
                throw new UserNotFoundException("No existe el usuario");
            }

        }



        private PetToReturn convertPetToReturn(Pet pet)
        {
            byte[] petImg = ImageSaver.GetIntance().getImage(pet.PetImage);
            PetToReturn petToReturn = new PetToReturn(pet.PetId, pet.Name, pet.PetType,petImg,pet.Gender, pet.Age, pet.Weight, pet.FriendlyPet, pet.HasVaccination,pet.Information);

            return petToReturn;
        }

        public void UpdatePetInfo(int clientId, int petId, PetBundle newPetInfo)
        {
            if (ExistsClient(clientId))
            {

                validatePetToUpdate(clientId, newPetInfo,petId);
                Pet pet = unitOfWork.PetRepository.Get(p => p.ClientId == clientId && p.PetId == petId).FirstOrDefault();

            
                Pet newPet = convertDTOPet(newPetInfo);

                UpdatePet(pet, newPet,newPetInfo.PetImage);
                unitOfWork.PetRepository.Update(pet);
                unitOfWork.Save();
            }
            else
            {
                throw new UserNotFoundException("No existe el usuario");
            }
        }

        private void UpdatePet(Pet pet, Pet newPet,sbyte [] petImage)
        {

            pet.Age = newPet.Age;
            pet.FriendlyPet = newPet.FriendlyPet;
            pet.Gender = newPet.Gender;
            pet.HasVaccination = newPet.HasVaccination;
            pet.Information = newPet.Information;
            pet.Name = newPet.Name;
            pet.Gender = newPet.Gender;
            pet.PetType = newPet.PetType;
            pet.Weight = newPet.Weight;
            if (newPet.PetImage != null)
            {
                pet.PetImage = newPet.PetImage;
            }

         
        }

        private void validatePetToUpdate(int clientId,PetBundle pet,int petId)
        {

            petValidator.validateHasAllFields(pet);
            petValidator.validateCorrectTypeFields(pet);
            petValidator.validatePetNotAlreadyAddedWhenUpdate(clientId, pet,petId);

        }

        public IEnumerable<Booking> GetBookingsOfClient(int clientId)
        {
            validator.validateUserExists(unitOfWork.ClientRepository.GetByID(clientId));
            IEnumerable<Booking> clientBookings = unitOfWork.BookingsRepository.Get(b => b.ClientId == clientId);
            return clientBookings;
        }

        public IEnumerable<Inform> GetClientInforms(int clientId)
        {
            Client c = unitOfWork.ClientRepository.GetByID(clientId);
            validator.validateUserExists(c);

            IEnumerable<Booking> bookings = unitOfWork.BookingsRepository.Get(b => b.ClientId == clientId);
            List<Inform> informs = new List<Inform>();

            foreach (Booking b in bookings)
            {
                IEnumerable<Inform> i = unitOfWork.InformsRepository.Get(inf => inf.BookingId == b.BookingId);
                informs.AddRange(i);
            }
            return informs;
        }

        public void DeleteClient(int clientId)
        {
            Client c = unitOfWork.ClientRepository.GetByID(clientId);
            validator.validateUserExists(c);
            validateCanDeleteClient(c);
            unitOfWork.ClientRepository.Delete(c);
            unitOfWork.Save();
        }

        private void validateCanDeleteClient(Client c)
        {
            IEnumerable<Booking> clientBookings = unitOfWork.BookingsRepository.Get(b => b.ClientId == c.ClientId);
            if(clientBookings.Count() > 0)
            {
                throw new UserHasBookingsException("El usuario tiene reservas registradas.");
            }

        }
    }
}
