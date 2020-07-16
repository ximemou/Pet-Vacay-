using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers
{
    [ExcludeFromCodeCoverage]
    public class WorkerBundle
    {
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Password { get; set; }
        public sbyte[] ProfileImage { get; set; }
        public string PhoneNumber { get; set; }
        public List<int> Disponibility { get; set; }
        public string ShortDescription { get; set; }
        public string Information { get; set; }
        public int WalkingPrice { get; set; }
        public int BoardingPrice { get; set; }
        public string Address { get; set; }

        public WorkerBundle() { }
        public WorkerBundle(string name, string surname, string newPassword, sbyte[] profileImg, string phoneNumber)
        {
            Name = name;
            Surname = surname;
            Password = newPassword;
            ProfileImage = profileImg;
            PhoneNumber = phoneNumber;
        }
    }
}
