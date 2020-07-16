using PetVacay.Data;
using PetVacay.DTO;
using PetVacay.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Validators
{
    public interface IUsersValidator
    {
        void validateUserIsNotRegistered(ClientDTO client = null, WorkerDTO worker = null);
        void validateHasAllFields(ClientDTO client = null, WorkerDTO worker = null);
        void validateAllFieldsHaveCorrectType(ClientDTO client = null, WorkerDTO worker = null);
        void validatePasswordsMatch(ClientDTO client = null, WorkerDTO worker = null);
        void validateUserExists(Client client = null, Worker worker = null);
        void validateStringData(ClientBundle client = null, WorkerBundle worker = null);
        void validatePhoneNumber(ClientBundle client = null, WorkerBundle worker = null);
    }
}
