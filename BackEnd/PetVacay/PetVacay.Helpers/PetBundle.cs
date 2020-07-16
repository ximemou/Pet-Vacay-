using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers
{
    [ExcludeFromCodeCoverage]
    public class PetBundle
    {
        public string Name { get; set; }
        public string PetType { get; set; }
        public int Age { get; set; }
        public float Weight { get; set; }
        public bool FriendlyPet { get; set; }
        public bool HasVaccination { get; set; }
        public string Gender { get; set; }
        public string Information { get; set; }
        public sbyte[] PetImage { get; set; }

        public PetBundle() { }

        public PetBundle(string name, string petType,string information,string gender, int age, float weight, bool friendlyPet, bool vaccination)
        {
            Name = name;
            PetType = petType;
            Age = age;
            Weight = weight;
            Information = information;
            Gender = gender;
            FriendlyPet = friendlyPet;
            HasVaccination = vaccination;
        }
    }
}
