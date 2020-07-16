using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PetVacay.Data
{
    [ExcludeFromCodeCoverage]
    public class WorkerImage
    {
        [Key]
        public int Id { get; set; }
        public string ImageUrl { get; set; }      
        public int WorkerId { get; set; }

        public WorkerImage() { }

        public WorkerImage(string url)
        {
            ImageUrl = url;
        }
    }
}
