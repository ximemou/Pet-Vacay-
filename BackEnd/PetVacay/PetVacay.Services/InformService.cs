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
    public class InformService : IInformService
    {
        private readonly IUnitOfWork unitOfWork;
        public InformService(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }

        public void RegisterNewInform(Inform inform)
        {
            validateNotRepeatedInform(inform);
            unitOfWork.InformsRepository.Insert(inform);
            unitOfWork.Save();
        }

        private void validateNotRepeatedInform(Inform inform)
        {
            DateTime informDate = inform.DateOfInform;
            IEnumerable<Inform> informs = unitOfWork.InformsRepository.Get(i => i.DateOfInform.Day == informDate.Day
                                            && i.DateOfInform.Month == informDate.Month
                                            && i.DateOfInform.Year == informDate.Year);
            if(informs.Count() > 0)
            {
                throw new RepeatedInformException("Ya se ha realizado un informe para esa fecha.");
            }
        }
    }
}
