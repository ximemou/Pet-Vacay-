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
    public class WorkerValidator : IUsersValidator
    {
        private readonly IUnitOfWork unitOfWork;

        public WorkerValidator(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }

        public void validateAllFieldsHaveCorrectType(ClientDTO client = null, WorkerDTO worker = null)
        {
            string regExpPatternForMail = @"^([\w-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([\w-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$";
            bool validMail = Regex.IsMatch(worker.Email, regExpPatternForMail);
            bool wrongPassword = worker.Password.Length < 8;
            bool wrongRepeatedPassword = worker.RepeatedPassword.Length < 8;
            bool disponibleAtLeastOneDay = worker.Disponibility != null && worker.Disponibility.Count > 0;

            if (!validMail || wrongPassword || wrongRepeatedPassword || (worker.IsWalker &&!disponibleAtLeastOneDay))
            {
                throw new WrongDataTypeException("Se han ingresado campos invalidos, " +
                    "el mail debe ser del formato mail@example.com y la contraseña debe tener mas de 8 caracteres.");
            }
            if (worker.IsWalker)
            {
                if (!disponibleAtLeastOneDay)
                {
                    throw new WrongDataTypeException("Se debe seleccionar al menos un dia disponible para pasear las mascotas");
                }
            }
        }

        public void validateHasAllFields(ClientDTO client = null, WorkerDTO worker = null)
        {
            if (worker.Email == null || worker.Password == null || worker.RepeatedPassword == null)
            {
                if(worker.IsWalker && worker.Disponibility == null)
                    throw new InsufficientDataException("No se han suministrado todos los datos requeridos para el registro del trabajador.");
                else
                    throw new InsufficientDataException("No se han suministrado todos los datos requeridos para el registro del trabajador.");
            }
        }

        public void validatePasswordsMatch(ClientDTO client = null, WorkerDTO worker = null)
        {
            if (worker.Password != worker.RepeatedPassword)
            {
                throw new PasswordsDoNotMatchException("La contraseña de verificacion no coincide con la original.");
            }
        }

        public void validatePhoneNumber(ClientBundle client = null, WorkerBundle worker = null)
        {
            bool wrongLength = worker.PhoneNumber.Length < 9;
            bool wrongFormat = !(worker.PhoneNumber.All(Char.IsDigit));

            if(wrongLength || wrongFormat)
            {
                throw new WrongDataTypeException("El numero de telefono debe contener como minimo 9 digitos.");
            }      
        }

        public void validateStringData(ClientBundle client = null, WorkerBundle worker = null)
        {
            bool wrongNameLength = worker.Name.Length < 4;
            bool wrongSurnameLength = worker.Surname.Length < 4;
            bool wrongPasswordLength = worker.Password.Length < 8;

            if (wrongNameLength || wrongSurnameLength || wrongPasswordLength)
            {
                throw new WrongDataTypeException("Los campos de texto no cumplen con el largo minimo.");
            }
        }

        public void validateUserExists(Client client = null, Worker worker = null)
        {
            if (worker == null)
            {
                throw new UserNotFoundException("No se ha encontrado el trabajador.");
            }
        }

        public void validateUserIsNotRegistered(ClientDTO client = null, WorkerDTO worker = null)
        {
            Client clientRegistered = unitOfWork.ClientRepository.Get(c => c.Email == worker.Email).FirstOrDefault();
            Worker workerRegistered = unitOfWork.WorkerRepository.Get(w => w.Email == worker.Email).FirstOrDefault();
            if (workerRegistered != null || clientRegistered != null)
            {
                throw new UserAlreadyRegisteredException("Ya existe un usuario con ese mail registrado en el sistema.");
            }
        }
    }
}
