resource "aws_subnet" "csye6225_public" {
  count                   = length(var.public_subnet_cidrs)
  vpc_id                  = aws_vpc.csye6225.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = var.availability_zones[count.index]
  map_public_ip_on_launch = true
  ipv6_cidr_block         = cidrsubnet(aws_vpc.csye6225.ipv6_cidr_block, 8, count.index)

  tags = {
    Name = "public-subnet-${count.index + 1}"
  }
}

resource "aws_subnet" "csye6225_private" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.csye6225.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = var.availability_zones[count.index]
  ipv6_cidr_block   = cidrsubnet(aws_vpc.csye6225.ipv6_cidr_block, 8, count.index + length(var.public_subnet_cidrs))

  tags = {
    Name = "private-subnet-${count.index + 1}"
  }
}
