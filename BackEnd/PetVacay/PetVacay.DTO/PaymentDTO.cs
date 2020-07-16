using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.DTO
{
    [ExcludeFromCodeCoverage]
    public class PaymentDTO
    {

        public string CreditCardNumber { get; set; }

        public int CCV { get; set; }

        public int CreditCardExpirationMonth { get; set; }

        public int CreditCardExpirationYear { get; set; }

        public int Amount { get; set; }

        public PaymentDTO() { }
    }
}
