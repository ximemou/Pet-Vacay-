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
    public class ReviewsController : ApiController
    {
        private readonly IReviewService ReviewService; 

        public ReviewsController(IReviewService reviewService)
        {
            ReviewService = reviewService;
        }

        [Route("api/reviews")]
        [HttpPost]
        public IHttpActionResult PostReview(Review review)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    return BadRequest(ModelState);
                }
                ReviewService.RegisterReview(review);
                JsonMessager message = new JsonMessager("Evaluacion realizada correctamente.");
                return ResponseMessage(Request.CreateResponse(HttpStatusCode.OK, message));
            }
            catch (BookingNotFoundException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
            catch (RepeatedReviewException ex)
            {
                return ResponseMessage(Request.CreateErrorResponse(HttpStatusCode.BadRequest, ex.Message));
            }
        }
    }
}
