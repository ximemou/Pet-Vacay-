using PetVacay.Data;
using PetVacay.Exceptions;
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
    public class InformsController : ApiController
    {
        private readonly IInformService InformService;

        public InformsController(IInformService _informService)
        {
            this.InformService = _informService;
        }

        [Route("api/informs")]
        [HttpPost]
        public IHttpActionResult PostInform(Inform newInform)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                InformService.RegisterNewInform(newInform);
                JsonMessager message = new JsonMessager("Informe creado exitosamente.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (RepeatedInformException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }
    }
}
