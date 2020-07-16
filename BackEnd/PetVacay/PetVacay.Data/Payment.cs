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
    public class Payment
    {

        [Key]
        public int Id { get; set; }
        public int BookingId { get; set; }
        public string CreditCardNumber { get; set; }
        public int CCV { get; set; }
        public int CreditCardExpirationMonth { get; set; }
        public int CreditCardExpirationYear { get; set; }
        public int Amount { get; set; }

        public Payment(int bookingId, string creditCardNumber, int ccv, int expirationMonth, int expirationYear, int amount)
        {
            this.Amount = amount;
            this.BookingId = bookingId;
            this.CCV = ccv;
            this.CreditCardExpirationMonth = expirationMonth;
            this.CreditCardExpirationYear = expirationYear;
            this.CreditCardNumber = creditCardNumber;
        }
    }
}
