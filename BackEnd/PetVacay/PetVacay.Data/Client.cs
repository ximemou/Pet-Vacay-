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
    public class Client
    {
        [Key]
        public int ClientId { get; set; }
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string ProfileImage { get; set; }
        public virtual List<Pet> PersonalPets { get; set; }
        public string PhoneNumber { get; set; }

        public Client()
        {
            PersonalPets = new List<Pet>();
        }

        public Client(string email, string pass)
        {
            Email = email;
            Password = pass;
        }
    }
}
