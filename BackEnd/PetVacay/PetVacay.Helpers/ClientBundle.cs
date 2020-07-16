using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers
{
    [ExcludeFromCodeCoverage]
    public class ClientBundle
    {
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Password { get; set; }
        public string PhoneNumber { get; set; }

        public ClientBundle() { }
        public ClientBundle(string name, string surname, string newPassword, string phoneNumber)
        {
            Name = name;
            Surname = surname;
            Password = newPassword;
            PhoneNumber = phoneNumber;
        }
    }
}
