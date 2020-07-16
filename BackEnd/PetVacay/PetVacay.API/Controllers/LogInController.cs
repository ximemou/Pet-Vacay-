using PetVacay.Services;
using PetVacay.Exceptions;
using PetVacay.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using PetVacay.Helpers.APIReturnClases;
using PetVacay.JsonMessage;
using System.Net.Mail;

namespace PetVacay.Controllers
{
    public class LogInController : ApiController
    {
        private readonly ILoginService logInService;

        public LogInController(ILoginService logInService)
        {
            this.logInService = logInService;
        }

        [Route("api/login")]
        [HttpPost]
        public IHttpActionResult DoLogin(LoginDTO loginData)
        {
            try
            {
                LogInInfoToReturn logInfo = logInService.ValidateLogin(loginData.Email, loginData.Password);
                return Ok(logInfo);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (WrongPasswordException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/resetPassword")]
        [HttpPost]
        public IHttpActionResult ResetUserPassword([FromUri(Name = "mail")] string userMail)
        {
            try
            {
                logInService.ResetPassword(userMail);
                JsonMessager message = new JsonMessager("Se ha enviado un mail con la nueva contraseña.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (SmtpException)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, "Error al enviar el correo."));
            }
        }
    }
}
