using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PetVacay.Data;
using PetVacay.DTO;
using PetVacay.Helpers;
using PetVacay.Exceptions;
using EveryPay.Data.Repository;
using System.Text.RegularExpressions;

namespace PetVacay.Validators
{  
    public class ClientValidator : IUsersValidator
    {
        private readonly IUnitOfWork unitOfWork;

        public ClientValidator(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }

        public void validateAllFieldsHaveCorrectType(ClientDTO client = null, WorkerDTO worker = null)
        {
            string regExpPatternForMail = @"^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$";
            bool validMail = Regex.IsMatch(client.Email, regExpPatternForMail);
            bool wrongPassword = client.Password.Length < 8;
            bool wrongRepeatedPassword = client.RepeatedPassword.Length < 8;

            if(!validMail || wrongPassword || wrongRepeatedPassword)
            {
                throw new WrongDataTypeException("Se han ingresado campos invalidos, " + 
                    "el mail debe ser del formato mail@example.com y la contraseña debe tener mas de 8 caracteres.");
            }
        }

        public void validateHasAllFields(ClientDTO client = null, WorkerDTO worker = null)
        {
            if (client.Email == null || client.Password == null || client.RepeatedPassword == null)
            {
                throw new InsufficientDataException("No se han suministrado todos los datos requeridos para el registro del cliente.");
            }
        }

        public void validatePasswordsMatch(ClientDTO client = null, WorkerDTO worker = null)
        {
            if (client.Password != client.RepeatedPassword)
            {
                throw new PasswordsDoNotMatchException("La contraseña de verificacion no coincide con la original.");
            }
        }

        public void validatePhoneNumber(ClientBundle client = null, WorkerBundle worker = null)
        {
            bool wrongLength = client.PhoneNumber.Length < 9;
            bool wrongFormat = !(client.PhoneNumber.All(Char.IsDigit));

            if (wrongLength || wrongFormat)
            {
                throw new WrongDataTypeException("El numero de telefono debe contener como minimo 9 digitos.");
            }
        }

        public void validateStringData(ClientBundle client = null, WorkerBundle worker = null)
        {
            bool wrongNameLength = client.Name.Length < 4;
            bool wrongSurnameLength = client.Surname.Length < 4;
            bool wrongPasswordLength = client.Password.Length < 8;

            if(wrongNameLength || wrongSurnameLength || wrongPasswordLength)
            {
                throw new WrongDataTypeException("Los campos de texto no cumplen con el largo minimo.");
            }
        }

        public void validateUserExists(Client client = null, Worker worker = null)
        {
            if (client == null)
            {
                throw new UserNotFoundException("No se ha encontrado el cliente.");
            }
        }

        public void validateUserIsNotRegistered(ClientDTO client = null, WorkerDTO worker = null)
        {
            Client clientRegistered = unitOfWork.ClientRepository.Get(c => c.Email ==client.Email).FirstOrDefault();
            Worker workerRegistered = unitOfWork.WorkerRepository.Get(w => w.Email == client.Email).FirstOrDefault();
            if (clientRegistered != null || workerRegistered != null)
            {
                throw new UserAlreadyRegisteredException("Ya existe un usuario con ese mail registrado en el sistema.");
            }
        }
    }
}
