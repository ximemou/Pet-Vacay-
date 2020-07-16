using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers.APIReturnClases
{
    [ExcludeFromCodeCoverage]
    public class ClientToReturn
    {
        public int ClientId { get; set; }
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public byte [] ProfileImage { get; set; }
        public virtual List<PetToReturn> PersonalPets { get; set; }
        public string PhoneNumber { get; set; }

        public ClientToReturn()
        {
            PersonalPets = new List<PetToReturn>();
        }

        public ClientToReturn(int id, string name, string surname, string email, string pass, byte [] image, 
            List<PetToReturn> pets, string phone)
        {
            ClientId = id;
            Name = name;
            Surname = surname;
            Email = email;
            Password = pass;
            ProfileImage = image;
            PersonalPets = pets;
            PhoneNumber = phone;
        }
    }
}
