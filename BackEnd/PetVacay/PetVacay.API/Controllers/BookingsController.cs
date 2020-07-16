using PetVacay.Data;
using PetVacay.DTO;
using PetVacay.Exceptions;
using PetVacay.Helpers.APIReturnClases;
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
    public class BookingsController : ApiController
    {
        private readonly IBookingService BookingService;

        public BookingsController(IBookingService bookingService)
        {
            this.BookingService = bookingService;
        }

        [Route("api/bookings")]
        [HttpPost]
        public IHttpActionResult PostBooking(BookingPaymentDTO bookingPaymentDto)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                int bookingId = BookingService.RegisterNewBooking(bookingPaymentDto);
                JsonMessager message = new JsonMessager(bookingId.ToString());
                return Ok(message);

            }
            catch (UserNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (BookingAlreadyRegisteredException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (DatesOverlapException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (DisponibilitiesNotMatchException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (InsufficientDataException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/bookings/{bookingId}")]
        [HttpDelete]
        public IHttpActionResult DeleteBooking(int bookingId)
        {
            try
            {
                BookingService.DeleteBooking(bookingId);
                JsonMessager message = new JsonMessager("Se ha cancelado su reserva.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (BookingNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (TimeExceededException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/bookings/{bookingId}/workerLocations")]
        [HttpGet]
        public IHttpActionResult GetBookingWorkerLocations(int bookingId)
        {
            try
            {
                WalkingLocationToReturn locations = BookingService.GetBookingWorkerLocations(bookingId);
                return Ok(locations);
            }
            catch (BookingNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/bookings/{bookingId}")]
        [HttpPut]
        public IHttpActionResult PutBooking(int bookingId, Location location)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                BookingService.UpdateWalkingInitialLocation(bookingId, location);
                JsonMessager message = new JsonMessager("Paseo de mascota comenzado.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (BookingNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }

        [Route("api/bookings/{bookingId}/informDays")]
        [HttpGet]
        public IHttpActionResult GetWorkerAvailableDaysToMakeInform(int bookingId)
        {
            try
            {
                IEnumerable<DateTime> dates = BookingService.GetWorkerAvailableDaysToMakeInform(bookingId);
                return Ok(dates);
            }
            catch (BookingNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }
    }
}
