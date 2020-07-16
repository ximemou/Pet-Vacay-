using PetVacay.Data;
using PetVacay.DTO;
using PetVacay.Helpers;
using PetVacay.Helpers.APIReturnClases;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Services
{
    public interface IWorkerService
    {
        int RegisterNewWorker(WorkerDTO newWorker);
        void UpdateWorkerInfo(int workerId, WorkerBundle workerInfo);
        void AddWorkerImage(int workerId, ImageBundle newImage);
        IEnumerable<ImageToReturn> getWorkerImages(int workerId);
        WorkerToReturn GetWorker(int workerId);
        IEnumerable<WorkerToReturn> GetWorkers();
        IEnumerable<WorkerToReturn> GetWorkersByFilter(bool both, string name, string neighbour);
        void UpdateWorkerLocation(int workerId, LocationBundle newLocation);
        IEnumerable<Booking> GetWorkerBookings(int workerId);
        IEnumerable<ReviewToReturn> GetWorkerReviews(int workerId);
        void DeleteWorker(int workerId);
        int GetWorkerAvailableInformsCount(int workerId);
    }
}
