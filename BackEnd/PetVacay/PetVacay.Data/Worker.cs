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
    public class Worker
    {
        [Key]
        public int WorkerId { get; set; }
        public string Name { get; set; }
        public string Surname { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string PhoneNumber { get; set; }
        public string ProfileImage { get; set; }
        public virtual List<WorkerImage> Images { get; set; }
        public bool IsWalker { get; set; }
        public virtual List<WeekDay> Disponibility { get; set; }
        public string ShortDescription { get; set; }
        public string Information { get; set; }
        public int WalkingPrice { get; set; }
        public int BoardingPrice { get; set; }
        public int ZIPCode { get; set; }
        public Location Location { get; set; }

        public Worker()
        {
            Images = new List<WorkerImage>();
            Disponibility = new List<WeekDay>();
        }

        public Worker(string email, string pass, bool isWalker, List<WeekDay> disponibility, int boardingPrice, string shortDescription, string information)
        {
            Email = email;
            Password = pass;
            IsWalker = isWalker;
            Disponibility = disponibility;
            BoardingPrice = boardingPrice;
            ShortDescription = shortDescription;
            Information = information;
        }
    }
}
