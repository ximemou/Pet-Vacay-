using PetVacay.DTO;
using PetVacay.JsonMessage;
using PetVacay.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace PetVacay.Controllers
{
    public class PaymentController : ApiController
    {
        private readonly IPaymentService paymentService;

        public PaymentController(IPaymentService paymentService)
        {
            this.paymentService = paymentService;
        }

        [Route("api/payments")]
        [HttpPost]
        public IHttpActionResult PostPayment(PaymentDTO paymentDto)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                paymentService.RegisterNewPayment(paymentDto);
                JsonMessager message = new JsonMessager("Pago realizado con exito.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));

            }
            catch (Exception ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }
    }
}
