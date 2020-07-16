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
using PetVacay.Helpers.APIReturnClases;
using PetVacay.Data;

namespace PetVacay.Controllers
{
    public class WorkersController : ApiController
    {
        private readonly IWorkerService WorkerService;

        public WorkersController(IWorkerService WorkerService)
        {
            this.WorkerService = WorkerService;
        }

        [Route("api/workers")]
        [HttpPost]
        public IHttpActionResult PostWorker(WorkerDTO worker)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                int workerId = WorkerService.RegisterNewWorker(worker);
                JsonMessager message = new JsonMessager(workerId.ToString());
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
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

        [Route("api/workers/{workerId}")]
        [HttpPut]
        public IHttpActionResult PutWorker(int workerId, WorkerBundle newWorkerInfo)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                WorkerService.UpdateWorkerInfo(workerId, newWorkerInfo);
                JsonMessager message = new JsonMessager("Trabajador actualizado correctamente.");
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

        [Route("api/workers/{workerId}/images")]
        [HttpPost]
        public IHttpActionResult PostWorkerImage(int workerId, ImageBundle newImage)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                WorkerService.AddWorkerImage(workerId, newImage);
                JsonMessager message = new JsonMessager("Imagen agregada correctamente.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/workers/{workerId}")]
        [HttpGet]
        public IHttpActionResult GetWorker(int workerId)
        {
            try
            {
                WorkerToReturn worker = WorkerService.GetWorker(workerId);
                return Ok(worker);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/workers/{workerId}/images")]
        [HttpGet]
        public IHttpActionResult GetWorkerImages(int workerId)
        {
            try
            {
                IEnumerable<ImageToReturn> workerImages = WorkerService.getWorkerImages(workerId);
                return Ok(workerImages);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/workers")]
        [HttpGet]
        public IHttpActionResult GetWorkers()
        {
            IEnumerable<WorkerToReturn> workers = WorkerService.GetWorkers();
            return Ok(workers);
        }

        [Route("api/workers/query")]
        [HttpGet]
        public IHttpActionResult GetWorkersByFilters([FromUri(Name = "both")] bool both, [FromUri(Name = "name")] string name = null,
            [FromUri(Name = "address")] string address = null)
        {
            try
            {
                IEnumerable<WorkerToReturn> workers = WorkerService.GetWorkersByFilter(both, name, address);
                return Ok(workers);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/workers/{workerId}/location")]
        [HttpPut]
        public IHttpActionResult PutWorkerLocation(int workerId, LocationBundle newLocation)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                WorkerService.UpdateWorkerLocation(workerId, newLocation);
                JsonMessager message = new JsonMessager("Ubicacion actualizada correctamente.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/workers/{workerId}/bookings")]
        [HttpGet]
        public IHttpActionResult GetWorkerBookings(int workerId)
        {
            try
            {
                IEnumerable<Booking> bookings = WorkerService.GetWorkerBookings(workerId);
                return Ok(bookings);
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/workers/{workerId}/reviews")]
        [HttpGet]
        public IHttpActionResult GetWorkerReviews(int workerId)
        {
            try
            {
                IEnumerable<ReviewToReturn> reviews = WorkerService.GetWorkerReviews(workerId);
                return Ok(reviews);
            }
            catch (NoReviewsForWorkerException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/workers/{workerId}")]
        [HttpDelete]
        public IHttpActionResult DeleteWorker(int workerId)
        {
            try
            {
                WorkerService.DeleteWorker(workerId);
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

        [Route("api/workers/{workerId}/hasBookingsToday")]
        [HttpGet]
        public IHttpActionResult GetWorkerBookingsToInformCount(int workerId)
        {
            try
            {
                int availableBookings = WorkerService.GetWorkerAvailableInformsCount(workerId);
                JsonMessager message = new JsonMessager(availableBookings.ToString());
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (WorkerHasNotBookingsToInformException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }
    }
}
