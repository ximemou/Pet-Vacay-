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
    public class Booking
    {
        [Key]
        public int BookingId { get; set; }
        public int ClientId { get; set; }
        public int WorkerId { get; set; }
        public int PetId { get; set; }
        public DateTime StartDate { get; set; }
        public DateTime FinishDate { get; set; }
        public DateTime MadeDate { get; set; }
        public Location InitialLocation { get; set; }
        public bool IsWalker { get; set; }
        public bool WalkingStarted { get; set; }

        public Booking() { }

        public Booking(int cId, int wId, int pId, DateTime start, DateTime finish, bool walkType)
        {
            ClientId = cId;
            WorkerId = wId;
            PetId = pId;
            StartDate = start;
            FinishDate = finish;
            IsWalker = walkType;
        }
    }
}
