using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using PetVacay.Adapters;

namespace PetVacay.Tests
{
    [TestClass]
    public class AdapterTest
    {
        [TestMethod]
        public void LocalizationTest1()
        {
            int zipCode = 0;
            IGeocoderAdapter localizationMgr = new GeocoderAdapter();
            string address = "Cuareim 1451";
            zipCode = localizationMgr.GetZipCodeFromAddress(address);

            Assert.AreEqual(11100, zipCode);
        }

        [TestMethod]
        [ExpectedException(typeof(NullReferenceException))]
        public void LocalizationTest2()
        {
            int zipCode = 0;
            IGeocoderAdapter localizationMgr = new GeocoderAdapter();
            string address = "Direccion inexistente";
            zipCode = localizationMgr.GetZipCodeFromAddress(address);
        }

        [TestMethod]
        public void LocalizationTest3()
        {
            int zipCode = 0;
            IGeocoderAdapter localizationMgr = new GeocoderAdapter();
            string address = "Pocitos Montevideo";
            zipCode = localizationMgr.GetZipCodeFromAddress(address);

            Assert.AreEqual(11300, zipCode);
        }
    }
}
