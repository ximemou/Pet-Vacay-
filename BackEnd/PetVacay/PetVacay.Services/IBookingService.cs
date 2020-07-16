using PetVacay.Data;
using PetVacay.DTO;
using PetVacay.Helpers.APIReturnClases;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Services
{
    public interface IBookingService
    {
        int RegisterNewBooking(BookingPaymentDTO bookingData);
        void DeleteBooking(int bookingId);
        WalkingLocationToReturn GetBookingWorkerLocations(int bookingId);
        void UpdateWalkingInitialLocation(int bookingId, Location location);
        IEnumerable<DateTime> GetWorkerAvailableDaysToMakeInform(int bookingId);
    }
}
