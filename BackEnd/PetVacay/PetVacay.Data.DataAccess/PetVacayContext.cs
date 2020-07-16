using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Data.DataAccess
{
    public class PetVacayContext : DbContext
    {
        public PetVacayContext() : base("name=PetVacayContext")
        {
            var ensureDLLIsCopied = System.Data.Entity.SqlServer.SqlProviderServices.Instance;
        }

        public DbSet<Client> Clients { get; set; }
        public DbSet<Worker> Workers { get; set; }
        public DbSet<Pet> Pets { get; set; }
        public DbSet<WeekDay> Disponibilities { get; set; }
        public DbSet<WorkerImage> ProfileImages { get; set; }
        public DbSet<Booking> Bookings { get; set; }
        public DbSet<Payment> Payments { get; set; }
        public DbSet<Inform> Informs { get; set; }
        public DbSet<Review> Reviews { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Client>().Property(c => c.Email).IsRequired();
            modelBuilder.Entity<Client>().Property(c => c.Password).IsRequired();

            modelBuilder.Entity<Worker>().Property(w => w.Email).IsRequired();
            modelBuilder.Entity<Worker>().Property(w => w.Password).IsRequired();
            modelBuilder.Entity<Worker>().Property(w => w.Location.Latitude).IsOptional();
            modelBuilder.Entity<Worker>().Property(w => w.Location.Longitude).IsOptional();

            modelBuilder.Entity<Pet>().Property(p => p.Name).IsRequired();
            modelBuilder.Entity<Pet>().Property(p => p.PetType).IsRequired();

            modelBuilder.Entity<Booking>().HasKey(b => new { b.BookingId});

            modelBuilder.Entity<Inform>().HasKey(i => new { i.InformId });

            modelBuilder.Entity<Review>().HasKey(r => new { r.ReviewId });

            base.OnModelCreating(modelBuilder);
        }
    }
}
