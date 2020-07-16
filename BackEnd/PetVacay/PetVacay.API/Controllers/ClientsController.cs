using PetVacay.Exceptions;
using PetVacay.Services;
using PetVacay.DTO;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using PetVacay.Helpers;
using PetVacay.JsonMessage;
using PetVacay.Data;
using PetVacay.Helpers.APIReturnClases;

namespace PetVacay.Controllers
{
    public class ClientsController : ApiController
    {
        private readonly IClientService ClientService;

        public ClientsController(IClientService ClientService)
        {
            this.ClientService = ClientService;
        }

        [Route("api/clients")]
        [HttpPost]
        public IHttpActionResult PostClient(ClientDTO client)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }

                int clientId = ClientService.RegisterNewClient(client);
                JsonMessager message = new JsonMessager(clientId.ToString());
                return Ok(message);
            }
            catch (InsufficientDataException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (PasswordsDoNotMatchException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (WrongDataTypeException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (UserAlreadyRegisteredException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/clients/{clientId}")]
        [HttpPut]
        public IHttpActionResult PutClient(int clientId, ClientBundle newClientInfo)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                ClientService.UpdateClientInfo(clientId, newClientInfo);
                JsonMessager message = new JsonMessager("Cliente actualizado correctamente.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (WrongDataTypeException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/clients/{clientId}/pets")]
        [HttpPost]
        public IHttpActionResult PostClientPets(int clientId, PetBundle petToAdd)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }

                ClientService.AddClientPet(clientId, petToAdd);
                JsonMessager message = new JsonMessager("Mascota agregada con exito.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (InsufficientDataException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (WrongDataTypeException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (PetAlreadyAddedException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/clients/{clientId}/pets")]
        [HttpGet]
        public IHttpActionResult GetClientPets(int clientId)
        {
            try
            {
                IEnumerable<Pet> values = ClientService.GetClientPets(clientId);
                return Ok(values);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.NotFound, ex.Message));
            }
        }

        [Route("api/clients/{clientId}/pets/{petId}")]
        [HttpGet]
        public IHttpActionResult GetClientPet(int clientId, int petId)
        {
            try
            {
                PetToReturn pet = ClientService.GetClientPet(clientId, petId);
                return Ok(pet);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.NotFound, ex.Message));
            }

        }

        [Route("api/clients/{clientId}")]
        [HttpGet]
        public IHttpActionResult GetClient(int clientId)
        {
            try
            {
                ClientToReturn client = ClientService.GetClient(clientId);
                return Ok(client);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/clients")]
        [HttpGet]
        public IHttpActionResult GetClients()
        {
            IEnumerable<Client> clients = ClientService.GetClients();

            return Ok(clients);
        }

        [Route("api/clients/{clientId}/profileImage")]
        [HttpGet]
        public IHttpActionResult GetClientImage(int clientId)
        {
            try
            {
                sbyte[] profileImage = (sbyte[])(Array)(ClientService.GetClientImage(clientId));
                return Ok(profileImage);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/clients/{clientId}/profileImage")]
        [HttpPost]
        public IHttpActionResult PostClientImage(int clientId, ImageBundle profileImage)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                ClientService.AddClientImage(clientId, profileImage);
                JsonMessager message = new JsonMessager("Imagen agregada correctamente.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }


        [Route("api/clients/{clientId}/pet/{petId}")]
        [HttpPut]
        public IHttpActionResult PutClientPet(int clientId, int petId, PetBundle newPetInfo)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                ClientService.UpdatePetInfo(clientId, petId, newPetInfo);
                JsonMessager message = new JsonMessager("Mascota actualizada correctamente.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (WrongDataTypeException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (PetAlreadyAddedException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (InsufficientDataException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/clients/{clientId}/bookings")]
        [HttpGet]
        public IHttpActionResult GetClientBookings(int clientId)
        {
            try
            {
                IEnumerable<Booking> bookings = ClientService.GetBookingsOfClient(clientId);
                return Ok(bookings);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.NotFound, ex.Message));
            }
        }

        [Route("api/clients/{clientId}/informs")]
        [HttpGet]
        public IHttpActionResult GetClientInforms(int clientId)
        {
            try
            {
                IEnumerable<Inform> informs = ClientService.GetClientInforms(clientId);
                return Ok(informs);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.NotFound, ex.Message));
            }
        }

        [Route("api/clients/{clientId}")]
        [HttpDelete]
        public IHttpActionResult DeleteClient(int clientId)
        {
            try
            {
                ClientService.DeleteClient(clientId);
                JsonMessager message = new JsonMessager("Se ha eliminado el usuario.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (UserHasBookingsException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }
    }
}
