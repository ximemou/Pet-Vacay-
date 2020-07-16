using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Helpers.APIReturnClases
{
    [ExcludeFromCodeCoverage]
    public class ReviewToReturn
    {
        public int Score { get; set; }
        public string Comment { get; set; }

        public ReviewToReturn() { }

        public ReviewToReturn(int score, string comment)
        {
            Score = score;
            Comment = comment;
        }
    }
}
