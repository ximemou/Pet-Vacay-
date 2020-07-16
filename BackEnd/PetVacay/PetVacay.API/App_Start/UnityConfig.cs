using Microsoft.Practices.Unity;
using PetVacay.DependencyResolver;
using System.Web.Compilation;
using System.Web.Http;
using Unity.WebApi;

namespace PetVacay
{
    public static class UnityConfig
    {
        public static void RegisterComponents()
        {
			var container = new UnityContainer();

            ComponentLoader.LoadContainer(container, ".\\bin", "PetVacay.*.dll");

            // register all your components with the container here
            // it is NOT necessary to register your controllers

            // e.g. container.RegisterType<ITestService, TestService>();

            GlobalConfiguration.Configuration.DependencyResolver = new UnityResolver(container);
        }
    }
}