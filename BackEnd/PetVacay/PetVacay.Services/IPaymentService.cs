using PetVacay.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Services
{
    public interface IPaymentService
    {
        void RegisterNewPayment(PaymentDTO paymentData);
    }
}
