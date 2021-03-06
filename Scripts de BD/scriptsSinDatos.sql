USE [PetVacayDB]
GO
/****** Object:  Table [dbo].[__MigrationHistory]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[__MigrationHistory](
	[MigrationId] [nvarchar](150) NOT NULL,
	[ContextKey] [nvarchar](300) NOT NULL,
	[Model] [varbinary](max) NOT NULL,
	[ProductVersion] [nvarchar](32) NOT NULL,
 CONSTRAINT [PK_dbo.__MigrationHistory] PRIMARY KEY CLUSTERED 
(
	[MigrationId] ASC,
	[ContextKey] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Bookings]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Bookings](
	[BookingId] [int] IDENTITY(1,1) NOT NULL,
	[ClientId] [int] NOT NULL,
	[WorkerId] [int] NOT NULL,
	[PetId] [int] NOT NULL,
	[StartDate] [datetime] NOT NULL,
	[FinishDate] [datetime] NOT NULL,
	[MadeDate] [datetime] NOT NULL,
	[InitialLocation_Latitude] [nvarchar](max) NULL,
	[InitialLocation_Longitude] [nvarchar](max) NULL,
	[IsWalker] [bit] NOT NULL,
	[WalkingStarted] [bit] NOT NULL,
 CONSTRAINT [PK_dbo.Bookings] PRIMARY KEY CLUSTERED 
(
	[BookingId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Clients]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Clients](
	[ClientId] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](max) NULL,
	[Surname] [nvarchar](max) NULL,
	[Email] [nvarchar](max) NOT NULL,
	[Password] [nvarchar](max) NOT NULL,
	[ProfileImage] [nvarchar](max) NULL,
	[PhoneNumber] [nvarchar](max) NULL,
 CONSTRAINT [PK_dbo.Clients] PRIMARY KEY CLUSTERED 
(
	[ClientId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Informs]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Informs](
	[InformId] [int] IDENTITY(1,1) NOT NULL,
	[BookingId] [int] NOT NULL,
	[DateOfInform] [datetime] NOT NULL,
	[InformData] [nvarchar](max) NULL,
 CONSTRAINT [PK_dbo.Informs] PRIMARY KEY CLUSTERED 
(
	[InformId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Payments]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Payments](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[BookingId] [int] NOT NULL,
	[CreditCardNumber] [nvarchar](max) NULL,
	[CCV] [int] NOT NULL,
	[CreditCardExpirationMonth] [int] NOT NULL,
	[CreditCardExpirationYear] [int] NOT NULL,
	[Amount] [int] NOT NULL,
 CONSTRAINT [PK_dbo.Payments] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Pets]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Pets](
	[PetId] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](max) NOT NULL,
	[PetType] [nvarchar](max) NOT NULL,
	[Age] [int] NOT NULL,
	[Weight] [real] NOT NULL,
	[FriendlyPet] [bit] NOT NULL,
	[HasVaccination] [bit] NOT NULL,
	[PetImage] [nvarchar](max) NULL,
	[ClientId] [int] NOT NULL,
	[Gender] [nvarchar](max) NULL,
	[Information] [nvarchar](max) NULL,
 CONSTRAINT [PK_dbo.Pets] PRIMARY KEY CLUSTERED 
(
	[PetId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Reviews]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Reviews](
	[ReviewId] [int] IDENTITY(1,1) NOT NULL,
	[Score] [int] NOT NULL,
	[Comment] [nvarchar](max) NULL,
	[BookingId] [int] NOT NULL,
 CONSTRAINT [PK_dbo.Reviews] PRIMARY KEY CLUSTERED 
(
	[ReviewId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[WeekDays]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[WeekDays](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Day] [int] NOT NULL,
	[WorkerId] [int] NOT NULL,
 CONSTRAINT [PK_dbo.WeekDays] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[WorkerImages]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[WorkerImages](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ImageUrl] [nvarchar](max) NULL,
	[WorkerId] [int] NOT NULL,
 CONSTRAINT [PK_dbo.WorkerImages] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Workers]    Script Date: 22/06/2017 16:05:32 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Workers](
	[WorkerId] [int] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](max) NULL,
	[Surname] [nvarchar](max) NULL,
	[Email] [nvarchar](max) NOT NULL,
	[Password] [nvarchar](max) NOT NULL,
	[PhoneNumber] [nvarchar](max) NULL,
	[ProfileImage] [nvarchar](max) NULL,
	[IsWalker] [bit] NOT NULL,
	[ShortDescription] [nvarchar](max) NULL,
	[Information] [nvarchar](max) NULL,
	[WalkingPrice] [int] NOT NULL,
	[BoardingPrice] [int] NOT NULL,
	[ZIPCode] [int] NOT NULL,
	[Location_Latitude] [nvarchar](max) NULL,
	[Location_Longitude] [nvarchar](max) NULL,
 CONSTRAINT [PK_dbo.Workers] PRIMARY KEY CLUSTERED 
(
	[WorkerId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
ALTER TABLE [dbo].[Bookings] ADD  DEFAULT ('1900-01-01T00:00:00.000') FOR [MadeDate]
GO
ALTER TABLE [dbo].[Bookings] ADD  DEFAULT ((0)) FOR [IsWalker]
GO
ALTER TABLE [dbo].[Bookings] ADD  DEFAULT ((0)) FOR [WalkingStarted]
GO
ALTER TABLE [dbo].[Workers] ADD  DEFAULT ((0)) FOR [WalkingPrice]
GO
ALTER TABLE [dbo].[Workers] ADD  DEFAULT ((0)) FOR [BoardingPrice]
GO
ALTER TABLE [dbo].[Workers] ADD  DEFAULT ((0)) FOR [ZIPCode]
GO
ALTER TABLE [dbo].[Pets]  WITH CHECK ADD  CONSTRAINT [FK_dbo.Pets_dbo.Clients_ClientId] FOREIGN KEY([ClientId])
REFERENCES [dbo].[Clients] ([ClientId])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Pets] CHECK CONSTRAINT [FK_dbo.Pets_dbo.Clients_ClientId]
GO
ALTER TABLE [dbo].[WeekDays]  WITH CHECK ADD  CONSTRAINT [FK_dbo.WeekDays_dbo.Workers_WorkerId] FOREIGN KEY([WorkerId])
REFERENCES [dbo].[Workers] ([WorkerId])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[WeekDays] CHECK CONSTRAINT [FK_dbo.WeekDays_dbo.Workers_WorkerId]
GO
ALTER TABLE [dbo].[WorkerImages]  WITH CHECK ADD  CONSTRAINT [FK_dbo.WorkerImages_dbo.Workers_WorkerId] FOREIGN KEY([WorkerId])
REFERENCES [dbo].[Workers] ([WorkerId])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[WorkerImages] CHECK CONSTRAINT [FK_dbo.WorkerImages_dbo.Workers_WorkerId]
GO
