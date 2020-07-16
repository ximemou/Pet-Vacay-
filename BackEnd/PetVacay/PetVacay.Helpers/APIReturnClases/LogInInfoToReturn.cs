using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers.APIReturnClases
{
    [ExcludeFromCodeCoverage]
    public class LogInInfoToReturn
    {
        public int userId { get; set; }
        public bool isWorker { get; set; }

        public LogInInfoToReturn(int id, bool worker)
        {
            userId = id;
            isWorker = worker;
        }
    }
}
