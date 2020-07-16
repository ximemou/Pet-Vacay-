using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers.APIReturnClases
{
    [ExcludeFromCodeCoverage]
    public class WorkerToReturn
    {
        public int WorkerId { get; set; }
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string PhoneNumber { get; set; }
        public byte [] ProfileImage { get; set; }
        public bool IsWalker { get; set; }
        public virtual List<string> Disponibility { get; set; }
        public string ShortDescription { get; set; }
        public string Information { get; set; }
        public int WalkingPrice { get; set; }
        public int BoardingPrice { get; set; }
        public byte[] BannerImage { get; set; }
        public LocationBundle Location { get; set; }
        public double AverageScore { get; set; }

        public WorkerToReturn()
        {
            Disponibility = new List<string>();
        }
        public WorkerToReturn(int id, string name, string surname, string email, string pass, 
            string phone, bool isWalker, List<string> disponibility, byte[] image, string sDescription, 
            string info, int bPrice, int wPrice, byte [] bImage, LocationBundle location, double score)
        {
            WorkerId = id;
            Name = name;
            Surname = surname;
            Email = email;
            Password = pass;
            PhoneNumber = phone;
            IsWalker = isWalker;
            Disponibility = disponibility;
            ProfileImage = image;
            ShortDescription = sDescription;
            Information = info;
            BoardingPrice = bPrice;
            WalkingPrice = wPrice;
            BannerImage = bImage;
            Location = location;
            AverageScore = score;
        }
    }
}
