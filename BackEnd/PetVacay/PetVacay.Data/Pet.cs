using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Data
{
    [ExcludeFromCodeCoverage]
    public class Pet
    {
        [Key]
        public int PetId { get; set; }
        public string Name { get; set; }
        public string PetType { get; set; }
        public int Age { get; set; }
        public float Weight { get; set; }
        public bool FriendlyPet { get; set; }
        public bool HasVaccination { get; set; }
        public string Gender { get; set; }
        public string PetImage { get; set; }
        public int ClientId { get; set; }
        public string Information { get; set; }

        public Pet() { }

        public Pet(string name, string petType, string gender, string information,int optionalAge = 0, float optionalWeight = 0, bool optionalFriendlyPet = false, bool optionalHasVaccination = false)
        {
            Name = name;
            PetType = petType;
            Age = optionalAge;
            Information = information;
            Weight = optionalWeight;
            FriendlyPet = optionalFriendlyPet;
            HasVaccination = optionalHasVaccination;
            Gender = gender;
        }
    }
}
