using PetVacay.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace EveryPay.Data.Repository
{
    public interface IUnitOfWork:IDisposable
    {
        IRepository<Client> ClientRepository { get;  }
        IRepository<Worker> WorkerRepository { get; }
        IRepository<Pet> PetRepository { get; }
        IRepository<WeekDay> DisponibilitiesRepository { get; }
        IRepository<WorkerImage> WorkerImagesRepository { get; }
        IRepository<Booking> BookingsRepository { get; }
        IRepository<Payment> PaymentRepository { get; }
        IRepository<Inform> InformsRepository { get; }
        IRepository<Review> ReviewsRepository { get; }
        void Save();
    }
}
