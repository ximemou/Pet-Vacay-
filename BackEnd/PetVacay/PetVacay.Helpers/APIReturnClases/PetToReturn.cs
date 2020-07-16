using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers.APIReturnClases
{
    [ExcludeFromCodeCoverage]
    public class PetToReturn
    {
        public int PetId { get; set; }
        public string Name { get; set; }
        public string PetType { get; set; }
        public int Age { get; set; }
        public float Weight { get; set; }
        public bool FriendlyPet { get; set; }
        public bool HasVaccination { get; set; }
        public byte [] PetImage { get; set; }
        public int ClientId { get; set; }
        public string Gender { get; set; }

    public string Information { get; set; }
        public PetToReturn() { }

        public PetToReturn(int id, string name, string petType, byte[] image, string gender,int optionalAge = 0, float optionalWeight = 0, bool optionalFriendlyPet = false, bool optionalHasVaccination = false,string information="")
        {
            PetId = id;
            Name = name;
            PetType = petType;
            Age = optionalAge;
            Weight = optionalWeight;
            FriendlyPet = optionalFriendlyPet;
            HasVaccination = optionalHasVaccination;
            PetImage = image;
            Gender = gender;
            Information = information;
        }
    }
}
