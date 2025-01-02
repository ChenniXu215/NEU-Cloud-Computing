resource "aws_route_table" "csye6225_public" {
  vpc_id = aws_vpc.csye6225.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.csye6225.id
  }


  tags = {
    Name = "csye6225-public-route-table"
  }
}

resource "aws_route_table_association" "csye6225_public" {
  count          = length(var.public_subnet_cidrs)
  subnet_id      = aws_subnet.csye6225_public[count.index].id
  route_table_id = aws_route_table.csye6225_public.id
}

resource "aws_route_table" "csye6225_private" {
  vpc_id = aws_vpc.csye6225.id

  tags = {
    Name = "csye6225-private-route-table"
  }
}

resource "aws_route_table_association" "csye6225_private" {
  count          = length(var.private_subnet_cidrs)
  subnet_id      = aws_subnet.csye6225_private[count.index].id
  route_table_id = aws_route_table.csye6225_private.id
}

resource "aws_route" "private_to_nat" {
  route_table_id         = aws_route_table.csye6225_private.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.nat.id
}

