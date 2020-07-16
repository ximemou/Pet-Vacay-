namespace PetVacay.Data.DataAccess.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class prueba : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Bookings",
                c => new
                    {
                        BookingId = c.Int(nullable: false, identity: true),
                        ClientId = c.Int(nullable: false),
                        WorkerId = c.Int(nullable: false),
                        PetId = c.Int(nullable: false),
                        StartDate = c.DateTime(nullable: false),
                        FinishDate = c.DateTime(nullable: false),
                        MadeDate = c.DateTime(nullable: false),
                        InitialLocation_Latitude = c.String(),
                        InitialLocation_Longitude = c.String(),
                        IsWalker = c.Boolean(nullable: false),
                        WalkingStarted = c.Boolean(nullable: false),
                    })
                .PrimaryKey(t => t.BookingId);
            
            CreateTable(
                "dbo.Clients",
                c => new
                    {
                        ClientId = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        Surname = c.String(),
                        Email = c.String(nullable: false),
                        Password = c.String(nullable: false),
                        ProfileImage = c.String(),
                        PhoneNumber = c.String(),
                    })
                .PrimaryKey(t => t.ClientId);
            
            CreateTable(
                "dbo.Pets",
                c => new
                    {
                        PetId = c.Int(nullable: false, identity: true),
                        Name = c.String(nullable: false),
                        PetType = c.String(nullable: false),
                        Age = c.Int(nullable: false),
                        Weight = c.Single(nullable: false),
                        FriendlyPet = c.Boolean(nullable: false),
                        HasVaccination = c.Boolean(nullable: false),
                        Gender = c.String(),
                        PetImage = c.String(),
                        ClientId = c.Int(nullable: false),
                        Information = c.String(),
                    })
                .PrimaryKey(t => t.PetId)
                .ForeignKey("dbo.Clients", t => t.ClientId, cascadeDelete: true)
                .Index(t => t.ClientId);
            
            CreateTable(
                "dbo.WeekDays",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        Day = c.Int(nullable: false),
                        WorkerId = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.Id)
                .ForeignKey("dbo.Workers", t => t.WorkerId, cascadeDelete: true)
                .Index(t => t.WorkerId);
            
            CreateTable(
                "dbo.Informs",
                c => new
                    {
                        InformId = c.Int(nullable: false, identity: true),
                        BookingId = c.Int(nullable: false),
                        DateOfInform = c.DateTime(nullable: false),
                        InformData = c.String(),
                    })
                .PrimaryKey(t => t.InformId);
            
            CreateTable(
                "dbo.Payments",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        BookingId = c.Int(nullable: false),
                        CreditCardNumber = c.String(),
                        CCV = c.Int(nullable: false),
                        CreditCardExpirationMonth = c.Int(nullable: false),
                        CreditCardExpirationYear = c.Int(nullable: false),
                        Amount = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.Id);
            
            CreateTable(
                "dbo.WorkerImages",
                c => new
                    {
                        Id = c.Int(nullable: false, identity: true),
                        ImageUrl = c.String(),
                        WorkerId = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.Id)
                .ForeignKey("dbo.Workers", t => t.WorkerId, cascadeDelete: true)
                .Index(t => t.WorkerId);
            
            CreateTable(
                "dbo.Reviews",
                c => new
                    {
                        ReviewId = c.Int(nullable: false, identity: true),
                        Score = c.Int(nullable: false),
                        Comment = c.String(),
                        BookingId = c.Int(nullable: false),
                    })
                .PrimaryKey(t => t.ReviewId);
            
            CreateTable(
                "dbo.Workers",
                c => new
                    {
                        WorkerId = c.Int(nullable: false, identity: true),
                        Name = c.String(),
                        Surname = c.String(),
                        Email = c.String(nullable: false),
                        Password = c.String(nullable: false),
                        PhoneNumber = c.String(),
                        ProfileImage = c.String(),
                        IsWalker = c.Boolean(nullable: false),
                        ShortDescription = c.String(),
                        Information = c.String(),
                        WalkingPrice = c.Int(nullable: false),
                        BoardingPrice = c.Int(nullable: false),
                        ZIPCode = c.Int(nullable: false),
                        Location_Latitude = c.String(),
                        Location_Longitude = c.String(),
                    })
                .PrimaryKey(t => t.WorkerId);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.WorkerImages", "WorkerId", "dbo.Workers");
            DropForeignKey("dbo.WeekDays", "WorkerId", "dbo.Workers");
            DropForeignKey("dbo.Pets", "ClientId", "dbo.Clients");
            DropIndex("dbo.WorkerImages", new[] { "WorkerId" });
            DropIndex("dbo.WeekDays", new[] { "WorkerId" });
            DropIndex("dbo.Pets", new[] { "ClientId" });
            DropTable("dbo.Workers");
            DropTable("dbo.Reviews");
            DropTable("dbo.WorkerImages");
            DropTable("dbo.Payments");
            DropTable("dbo.Informs");
            DropTable("dbo.WeekDays");
            DropTable("dbo.Pets");
            DropTable("dbo.Clients");
            DropTable("dbo.Bookings");
        }
    }
}
