using PetVacay.DependencyResolver;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.ComponentModel.Composition;

namespace PetVacay.Services
{
    [Export(typeof(IComponent))]
    public class DependencyResolver : IComponent
    {
        public void SetUp(IRegisterComponent registerComponent)
        {
            registerComponent.RegisterType<IClientService, ClientService>();
            registerComponent.RegisterType<IWorkerService, WorkerService>();
            registerComponent.RegisterType<ILoginService, LogInService>();
            registerComponent.RegisterType<IBookingService, BookingService>();
            registerComponent.RegisterType<IPaymentService, PaymentService>();
            registerComponent.RegisterType<IInformService, InformService>();
            registerComponent.RegisterType<IReviewService, ReviewService>();
        }
    }
}
