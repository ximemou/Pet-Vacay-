using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.DTO
{
    [ExcludeFromCodeCoverage]
    public class WorkerDTO
    {
        public string Email { get; set; }
        public string Password { get; set; }
        public string RepeatedPassword { get; set; }
        public bool IsWalker { get; set; }
        public List<DayOfWeek> Disponibility { get; set; }
        public string Latitude { get; set; }
        public string Longitude { get; set; }

        public WorkerDTO()
        {
            Disponibility = new List<DayOfWeek>();
        }
    }
}
