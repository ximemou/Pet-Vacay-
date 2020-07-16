using PetVacay.Data;
using PetVacay.DTO;
using PetVacay.Helpers;
using PetVacay.Helpers.APIReturnClases;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Services
{
    public interface IClientService
    {
        int RegisterNewClient(ClientDTO newClient);
        void AddClientPet(int clientId, PetBundle petData);
        void UpdateClientInfo(int clientId, ClientBundle newClientInfo);
        IEnumerable<Pet> GetClientPets(int clientId);
        ClientToReturn GetClient(int clientId);
        IEnumerable<Client> GetClients();
        byte[] GetClientImage(int clientId);
        void AddClientImage(int clientId, ImageBundle profileImage);
        PetToReturn GetClientPet(int clientId, int petId);
        void UpdatePetInfo(int clientId, int petId, PetBundle newPetInfo);
        IEnumerable<Booking> GetBookingsOfClient(int clientId);
        IEnumerable<Inform> GetClientInforms(int clientId);
        void DeleteClient(int clientId);

    }
}
