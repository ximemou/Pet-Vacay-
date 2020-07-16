using PetVacay.Helpers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Validators
{
    public interface IPetValidator
    {
        void validateHasAllFields(PetBundle petData);
        void validateCorrectTypeFields(PetBundle petData);
        void validatePetNotAlreadyAdded(int clientId, PetBundle petData);
        void validatePetNotAlreadyAddedWhenUpdate(int clientId, PetBundle pet,int petId);

    }
}
