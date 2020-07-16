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
    public class WeekDay
    {
        [Key]
        public int Id { get; set; }
        public DayOfWeek Day { get; set; }
        public int WorkerId { get; set; }

        public WeekDay() { }

        public WeekDay(DayOfWeek day)
        {
            Day = day;
        }
    }
}
