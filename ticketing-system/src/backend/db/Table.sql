

create table Client
(
    clientID SERIAL PRIMARY KEY,
    clientName VARCHAR(255) NOT NULL,
    clientEmployee VARCHAR(255)
);

create table Employee
(
    employeeID SERIAL PRIMARY KEY,
    clientID INTEGER NOT NULL REFERENCES Client(clientID),
    employeeName VARCHAR(255) NOT NULL,
    employeeEmail VARCHAR(255) NOT NULL
);

create table Ticket
(
    clientID INTEGER NOT NULL REFERENCES Client(clientID),
    ticketID SERIAL PRIMARY KEY,
    ticketSender INTEGER NOT NULL REFERENCES Employee(employeeID),
    TechnicianID INTEGER REFERENCES Technician(technicianID),
    ticketBody TEXT,
    createdAt TIMESTAMP DEFAULT NOW(),
    ticketStart TIMESTAMP WITHOUT TIME ZONE,
    ticketFinish TIMESTAMP WITHOUT TIME ZONE,
    ticketStatus INTEGER REFERENCES TicketStatus(statusID),
    ticketDuration INTERVAL
);

create table TicketTimeLog
(
    logID SERIAL PRIMARY KEY,
    ticketID INTEGER REFERENCES Ticket(ticketID),
    eventType VARCHAR(50), -- things such as "START", "REASSIGNED", "CLOSED"
    eventTime TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

create table SystemConfig (
    configKey VARCHAR(255) PRIMARY KEY,
    configValue VARCHAR(255) NOT NULL
);

create table Technician (
    technicianID SERIAL PRIMARY KEY,
    technicianName VARCHAR(255) NOT NULL,
    technicianEmail VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

create table EmployeeRole (
    roleID SERIAL PRIMARY KEY,
    roleName VARCHAR(50) UNIQUE NOT NULL
);

create table UserRole(
    userID INTEGER NOT NULL REFERENCES Employee(employeeID),
    roleID INTEGER NOT NULL REFERENCES EmployeeRole(roleID),
    PRIMARY KEY (userID, roleID)
);

create table TicketStatus(
    statusID SERIAL PRIMARY KEY,
    statusName VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO SystemConfig(configKey, configValues) VALUES 
('mail_user', ''),
('mail_pass',''),
('smtp_host','smtpserver'),
('smtp_port','587'),
('imap_host',''),
('imap_port','993');