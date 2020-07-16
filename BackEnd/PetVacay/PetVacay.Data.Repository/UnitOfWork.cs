using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using PetVacay.Data;
using PetVacay.Data.DataAccess;

namespace EveryPay.Data.Repository
{
    public class UnitOfWork : IUnitOfWork
    {
        private PetVacayContext context;
       
        private GenericRepository<Client> clientRepository;

        private GenericRepository<Worker> workerRepository;

        private GenericRepository<Pet> petRepository;

        private GenericRepository<WeekDay> disponibilityRepository;

        private GenericRepository<WorkerImage> workerImagesRepository;
        private GenericRepository<Booking> bookingsRepository;

        private GenericRepository<Payment> paymentRepository;
        private GenericRepository<Inform> informsRepository;

        private GenericRepository<Review> reviewsRepository;

        private bool disposed = false;

        public UnitOfWork()
        {
            this.context = new PetVacayContext();
        }

        IRepository<Client> IUnitOfWork.ClientRepository
        {
            get
            {
                if (this.clientRepository == null)
                {
                    this.clientRepository = new GenericRepository<Client>(context);
                }
                return clientRepository;
            }
        }

        public IRepository<Worker> WorkerRepository
        {
            get
            {
                if (this.workerRepository == null)
                {
                    this.workerRepository = new GenericRepository<Worker>(context);
                }
                return workerRepository;
            }
        }

        public IRepository<Pet> PetRepository
        {
            get
            {
                if (this.petRepository == null)
                {
                    this.petRepository = new GenericRepository<Pet>(context);
                }
                return petRepository;
            }
        }

        public IRepository<WeekDay> DisponibilitiesRepository
        {
            get
            {
                if (this.disponibilityRepository == null)
                {
                    this.disponibilityRepository = new GenericRepository<WeekDay>(context);
                }
                return disponibilityRepository;
            }
        }

        public IRepository<WorkerImage> WorkerImagesRepository
        {
            get
            {
                if (this.workerImagesRepository == null)
                {
                    this.workerImagesRepository = new GenericRepository<WorkerImage>(context);
                }
                return workerImagesRepository;
            }
        }

        public IRepository<Booking> BookingsRepository
        {
            get
            {
                if (this.bookingsRepository == null)
                {
                    this.bookingsRepository = new GenericRepository<Booking>(context);
                }
                return bookingsRepository;
            }
        }

        public IRepository<Payment> PaymentRepository
        {
            get
            {
                if (this.paymentRepository == null)
                {
                    this.paymentRepository = new GenericRepository<Payment>(context);
                }
                return paymentRepository;
            }
        }
        public IRepository<Inform> InformsRepository
        {
            get
            {
                if (this.informsRepository == null)
                {
                    this.informsRepository = new GenericRepository<Inform>(context);
                }
                return informsRepository;
            }
        }
        public IRepository<Review> ReviewsRepository
        {
            get
            {
                if (this.reviewsRepository == null)
                {
                    this.reviewsRepository = new GenericRepository<Review>(context);
                }
                return reviewsRepository;
            }
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!this.disposed)
            {
                if (disposing)
                {
                    context.Dispose();
                }
            }
            this.disposed = true;
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        public void Save()
        {
            context.SaveChanges();
        }
    }
}
