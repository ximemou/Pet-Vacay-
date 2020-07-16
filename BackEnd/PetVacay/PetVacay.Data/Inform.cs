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
    public class Inform
    {
        [Key]
        public int InformId { get; set; }
        public int BookingId { get; set; }
        public DateTime DateOfInform { get; set; }
        public string InformData { get; set; }

        public Inform() { }

        public Inform(int bookingId, DateTime dateInform, string data)
        {
            BookingId = bookingId;
            DateOfInform = dateInform;
            InformData = data;
        }


    }
}
