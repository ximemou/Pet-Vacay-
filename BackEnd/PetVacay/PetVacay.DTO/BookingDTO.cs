using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.DTO
{
    [ExcludeFromCodeCoverage]
    public class BookingDTO
    {
        public int ClientId { get; set; }
        public int WorkerId { get; set; }
        public int PetId { get; set; }
        public DateTime StartDate { get; set; }
        public DateTime FinishDate { get; set; }
        public bool IsWalker { get; set; }


        public BookingDTO() { }
    }
}
