using PetVacay.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Services
{
    public interface IReviewService
    {
        void RegisterReview(Review newReview);
    }
}
