using EveryPay.Data.Repository;
using PetVacay.Data;
using PetVacay.Exceptions;
using PetVacay.Helpers.APIReturnClases;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Services
{
    public class LogInService : ILoginService
    {
        private readonly IUnitOfWork unitOfWork;

        public LogInService(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }

        public LogInInfoToReturn ValidateLogin(string userMail, string userPass)
        {
            Client client = unitOfWork.ClientRepository.Get(c => c.Email == userMail).FirstOrDefault();
            Worker worker = unitOfWork.WorkerRepository.Get(w => w.Email == userMail).FirstOrDefault();

            if(client != null)
            {
                checkClientPassword(client, userPass);
                return new LogInInfoToReturn(client.ClientId, false);
            }
            else if(worker != null)
            {
                checkWorkerPassword(worker, userPass);
                return new LogInInfoToReturn(worker.WorkerId, true);
            }
            else
            {
                throw new UserNotFoundException("No se pudo realizar el login, el usuario no existe.");
            }
        }

        private void checkClientPassword(Client client, string userPass)
        {
            if(client.Password != userPass)
            {
                throw new WrongPasswordException("La contraseña no conicide con la de ese cliente.");
            }
        }

        private void checkWorkerPassword(Worker worker, string userPass)
        {
            if (worker.Password != userPass)
            {
                throw new WrongPasswordException("La contraseña no conicide con la de ese trabajador.");
            }
        }

        public void ResetPassword(string userMail)
        {
            string newPassword = Guid.NewGuid().ToString();
            Worker worker = unitOfWork.WorkerRepository.Get(w => w.Email == userMail).FirstOrDefault();
            Client client = unitOfWork.ClientRepository.Get(c => c.Email == userMail).FirstOrDefault();
            if (worker != null)
            {
                resetWorkerPassword(worker, newPassword);
            }
            else if(client != null)
            {
                resetClientPassword(client, newPassword);
            }
        }

        private void resetWorkerPassword(Worker worker, string newPassword)
        {
            worker.Password = newPassword;
            unitOfWork.WorkerRepository.Update(worker);
            unitOfWork.Save();
            sendMail(worker.Email, newPassword);
        }

        private void resetClientPassword(Client client, string newPassword)
        {
            client.Password = newPassword;
            unitOfWork.ClientRepository.Update(client);
            unitOfWork.Save();
            sendMail(client.Email, newPassword);
        }

        private void sendMail(string email, string newPassword)
        {
            string toMail = email;
            string fromMail = "petvacaystaff@hotmail.com";
            string subject = "PetVacay: Cambia tu contraseña";
            string message = "Su nueva contraseña es: \n " + newPassword;

            SmtpClient server = new SmtpClient("smtp.live.com", 25);
            server.Credentials = new NetworkCredential(fromMail, "petVacay2017");
            server.EnableSsl = true;

            MailMessage mail = new MailMessage();
            mail.To.Add(new MailAddress(toMail));
            mail.From = new MailAddress(fromMail, "PetVacay Staff");
            mail.Subject = subject;
            mail.Body = message;

            server.Send(mail);
        }
    }
}
