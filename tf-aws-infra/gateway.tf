resource "aws_internet_gateway" "csye6225" {
  vpc_id = aws_vpc.csye6225.id

  tags = {
    Name = "csye6225-igw"
  }
}

resource "aws_eip" "nat_gateway" {
  tags = {
    Name = "nat-gateway-eip"
  }
}

resource "aws_nat_gateway" "nat" {
  allocation_id     = aws_eip.nat_gateway.id
  subnet_id         = aws_subnet.csye6225_public[0].id
  connectivity_type = "public"

  tags = {
    Name = "nat-gateway"
  }
}