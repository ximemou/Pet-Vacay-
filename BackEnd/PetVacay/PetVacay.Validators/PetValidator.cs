using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PetVacay.Helpers;
using EveryPay.Data.Repository;
using PetVacay.Exceptions;
using PetVacay.Data;
using PetVacay.Enumerators;

namespace PetVacay.Validators
{
    public class PetValidator : IPetValidator
    {
        private readonly IUnitOfWork unitOfWork;

        public PetValidator(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }
        public void validateCorrectTypeFields(PetBundle petData)
        {
            bool wrongName = petData.Name.Length < 3;
            if (wrongName)
            {
                throw new WrongDataTypeException("El nombre de la mascota debe ser mayor a 3 caracteres.");
            }
        }

        public void validateHasAllFields(PetBundle petData)
        {
            if(petData.Name == null)
            {
                throw new InsufficientDataException("Debe proporcionar un nombre para su mascota.");
            }
        }

        public void validatePetNotAlreadyAdded(int clientId, PetBundle petData)
        {
            Pet pet = unitOfWork.PetRepository.Get(p => p.ClientId == clientId && p.Name == petData.Name).FirstOrDefault();
            if(pet != null)
            {
                throw new PetAlreadyAddedException("El usuario ya tiene una mascota llamada " + petData.Name);
            }
        }

        public void validatePetNotAlreadyAddedWhenUpdate(int clientId,PetBundle petData,int petId)
        {

            Pet pet = unitOfWork.PetRepository.Get(p => p.ClientId == clientId && p.Name == petData.Name).FirstOrDefault();
            if (pet != null)
            {
                if (pet.PetId != petId)
                {
                    throw new PetAlreadyAddedException("El usuario ya tiene una mascota llamada " + petData.Name);
                }

            }

        }


    


    }
}
