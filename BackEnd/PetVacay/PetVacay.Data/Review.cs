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
    public class Review
    {
        [Key]
        public int ReviewId { get; set; }
        public int Score { get; set; }
        public string Comment { get; set; }
        public int BookingId { get; set; }

        public Review() { }

        public Review(int score, string comment, int bookingId)
        {
            Score = score;
            Comment = comment;
            BookingId = bookingId;
        }
    }
}
