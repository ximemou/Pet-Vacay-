using PetVacay.Helpers.APIReturnClases;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Services
{
    public interface ILoginService
    {
        LogInInfoToReturn ValidateLogin(string userMail, string userPass);
        void ResetPassword(string userMail);
    }
}
