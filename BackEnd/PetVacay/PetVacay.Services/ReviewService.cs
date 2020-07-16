using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PetVacay.Data;
using EveryPay.Data.Repository;
using PetVacay.Exceptions;

namespace PetVacay.Services
{
    public class ReviewService : IReviewService
    {
        private readonly IUnitOfWork unitOfWork;

        public ReviewService(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }

        public void RegisterReview(Review newReview)
        {
            Booking b = unitOfWork.BookingsRepository.GetByID(newReview.BookingId);
            validateBookingExists(b);
            validateReviewNotRepeated(newReview);

            unitOfWork.ReviewsRepository.Insert(newReview);
            unitOfWork.Save();
        }

        private void validateBookingExists(Booking b)
        {
            if(b == null)
            {
                throw new BookingNotFoundException("No existe la reserva");
            }
        }
        private void validateReviewNotRepeated(Review newReview)
        {
            IEnumerable<Review> bookingReviews = unitOfWork.ReviewsRepository.Get(r => r.BookingId == newReview.BookingId);
            if(bookingReviews.Count() > 0)
            {
                throw new RepeatedReviewException("Ya se ha calificado esta reserva");
            }
        }
    }
}
